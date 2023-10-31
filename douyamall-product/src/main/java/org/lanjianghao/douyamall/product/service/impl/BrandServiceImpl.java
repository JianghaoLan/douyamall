package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.douyamall.product.dao.CategoryBrandRelationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.BrandDao;
import org.lanjianghao.douyamall.product.entity.BrandEntity;
import org.lanjianghao.douyamall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");

        if (!StringUtils.hasLength(key)) {
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<BrandEntity>()
            );
            return new PageUtils(page);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>().eq("brand_id", key).or().like("name", key)
        );
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public boolean updateCascadeById(BrandEntity brand) {
        this.updateById(brand);
        if (brand.getName() != null) {
            categoryBrandRelationDao.updateBrand(brand.getBrandId(), brand.getName());

            //TODO 更新其它关联
        }
        return true;
    }

}