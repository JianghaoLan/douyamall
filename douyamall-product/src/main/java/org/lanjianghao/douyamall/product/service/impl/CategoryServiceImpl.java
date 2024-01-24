package org.lanjianghao.douyamall.product.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.lanjianghao.douyamall.product.dao.CategoryBrandRelationDao;
import org.lanjianghao.douyamall.product.redis.CategoryRepository;
import org.lanjianghao.douyamall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.CategoryDao;
import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import org.lanjianghao.douyamall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查找所有分类，返回树形结构
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);
        int maxCatLevel = queryMaxCategoryLevel();
        List<Map<Long, CategoryEntity>> levelsCategories = new ArrayList<>(maxCatLevel);
        for (int i = 0; i < maxCatLevel; i++) {
            levelsCategories.add(new HashMap<>());
        }
        entities.forEach(entity -> {
            entity.setChildren(entity.getCatLevel() == maxCatLevel ? null : new ArrayList<>());
            levelsCategories.get(entity.getCatLevel() - 1).put(entity.getCatId(), entity);
        });

        for (int i = maxCatLevel - 2; i >= 0; i--) {
            Map<Long, CategoryEntity> curLevelCategories = levelsCategories.get(i);
            List<CategoryEntity> childLevelCategories = new ArrayList<>(levelsCategories.get(i + 1).values());
            childLevelCategories.sort(Comparator.comparingInt(entity -> entity.getSort() == null ? 0 : entity.getSort()));
            for (CategoryEntity entity : childLevelCategories) {
                long parentCid = entity.getParentCid();
                if (curLevelCategories.containsKey(parentCid)) {
                    curLevelCategories.get(parentCid).getChildren().add(entity);
                }
            }
        }
        List<CategoryEntity> topLevelCategories = new ArrayList<>(levelsCategories.get(0).values());
        topLevelCategories.sort(Comparator.comparingInt(entity -> entity.getSort() == null ? 0 : entity.getSort()));

        return topLevelCategories;
    }

    @CacheEvict(cacheNames = "category", allEntries = true)
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查要删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    private void findCategoryPath(long categoryId, List<Long> path) {
        CategoryEntity category = this.getById(categoryId);
        if (category.getParentCid() != 0) {
            findCategoryPath(category.getParentCid(), path);
        }
        path.add(categoryId);
    }

    @Override
    public Long[] findCategoryPath(long categoryId) {
        List<Long> path = new ArrayList<>();
        findCategoryPath(categoryId, path);
        Long[] pathArray = new Long[path.size()];
        return path.toArray(pathArray);
    }

    @CacheEvict(cacheNames = "category", allEntries = true)
    @Override
    @Transactional
    public boolean updateCascadeById(CategoryEntity category) {
        this.updateById(category);
        if (category.getName() != null) {
            categoryBrandRelationDao.updateCategory(category.getCatId(), category.getName());
        }
        return true;
    }

    @Cacheable(value = "category", key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> listTopLevelCategories() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
//        return null;
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> result = null;
        try {
            result = categoryRepository.getIndexCatalog();
        } catch (JsonProcessingException ignored) {}

        if (result != null) {
            return result;
        }

        RLock lock = redisson.getLock("indexCatalogLock");
        lock.lock();
        try {
            try {
                result = categoryRepository.getIndexCatalog();
            } catch (JsonProcessingException ignored) {}
            if (result != null) {
                return result;
            }

            //查询数据库
//            System.out.println("查询了数据库");
            result = getCatalogJsonFromDb();
            //写入redis
            try {
                categoryRepository.setIndexCatalog(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
////        System.out.println("缓存不存在，将要查询数据库");
//        //本地锁
//        synchronized (this) {
//
//        }

        return result;
    }

    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
        List<CategoryEntity> categoryEntities = this.list();

        Map<String, List<Catalog2Vo>> catL1Map = new HashMap<>();
        Map<String, Catalog2Vo> catL2Map = new HashMap<>();
        List<Catalog2Vo.Catalog3> catL3List = new ArrayList<>();
        categoryEntities.forEach(cat -> {
            if (cat.getCatLevel() == 1) {
                catL1Map.put(cat.getCatId().toString(), new ArrayList<>());
            } else if (cat.getCatLevel() == 2) {
                Catalog2Vo vo = new Catalog2Vo(cat.getParentCid().toString(),
                        new ArrayList<>(), cat.getCatId().toString(), cat.getName());
                catL2Map.put(cat.getCatId().toString(), vo);
            } else if (cat.getCatLevel() == 3) {
                Catalog2Vo.Catalog3 catalog3 = new Catalog2Vo.Catalog3(cat.getParentCid().toString(),
                        cat.getCatId().toString(), cat.getName());
                catL3List.add(catalog3);
            }
        });

        catL3List.forEach(catL3 -> {
            catL2Map.get(catL3.getCatalog2Id()).getCatalog3List().add(catL3);
        });
        catL2Map.forEach((key, value) -> {
            catL1Map.get(value.getCatalog1Id()).add(value);
        });

        return catL1Map;
    }

    private int queryMaxCategoryLevel() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(max(cat_level), 0) as maxCatLevel");
        return (int) (long) getMap(queryWrapper).get("maxCatLevel");
    }
}