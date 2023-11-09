package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.douyamall.product.dao.BrandDao;
import org.lanjianghao.douyamall.product.dao.CategoryDao;
import org.lanjianghao.douyamall.product.entity.BrandEntity;
import org.lanjianghao.douyamall.product.service.BrandService;
import org.lanjianghao.douyamall.product.vo.BriefBrandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.CategoryBrandRelationDao;
import org.lanjianghao.douyamall.product.entity.CategoryBrandRelationEntity;
import org.lanjianghao.douyamall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> listCategory(Long brandId) {
        return this.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public boolean saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        String brandName = brandDao.selectById(brandId).getName();
        String catelogName = categoryDao.selectById(catelogId).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(catelogName);

        return this.save(categoryBrandRelation);
    }

    @Override
    public List<BrandEntity> listBrands(Long catId) {
        List<CategoryBrandRelationEntity> relationEntities =
                this.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<Long> brandIds = relationEntities.stream()
                .map(CategoryBrandRelationEntity::getBrandId)
                .collect(Collectors.toList());
        return brandService.listByIds(brandIds);
    }

}