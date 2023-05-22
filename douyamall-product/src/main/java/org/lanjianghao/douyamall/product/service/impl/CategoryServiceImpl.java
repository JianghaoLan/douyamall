package org.lanjianghao.douyamall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.CategoryDao;
import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import org.lanjianghao.douyamall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查要删除的菜单是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    private int queryMaxCategoryLevel() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(max(cat_level), 0) as maxCatLevel");
        return (int) (long) getMap(queryWrapper).get("maxCatLevel");
    }
}