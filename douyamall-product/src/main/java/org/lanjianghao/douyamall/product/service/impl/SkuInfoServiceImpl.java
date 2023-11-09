package org.lanjianghao.douyamall.product.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.SkuInfoDao;
import org.lanjianghao.douyamall.product.entity.SkuInfoEntity;
import org.lanjianghao.douyamall.product.service.SkuInfoService;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        /**
         * key: '华为',//检索关键字
         * catelogId: 0,
         * brandId: 0,
         * min: 0,
         * max: 0
         */
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String)params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and(wrapper -> wrapper.eq("sku_id", key).or().like("sku_name", key)
                    .or().like("sku_desc", key).or().like("sku_title", key));
        }

        String catelogId = (String)params.get("catelogId");
        if (StringUtils.hasLength(catelogId) && Integer.parseInt(catelogId) != 0) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String)params.get("brandId");
        if (StringUtils.hasLength(brandId) && Integer.parseInt(brandId) != 0) {
            queryWrapper.eq("brand_id", brandId);
        }

        String min = (String)params.get("min");
        if (StringUtils.hasLength(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String)params.get("max");
        if (StringUtils.hasLength(max) && new BigDecimal(max).compareTo(new BigDecimal("0")) > 0) {
            BigDecimal maxBD = new BigDecimal(max);
            if (maxBD.compareTo(new BigDecimal("0")) > 0) {
                queryWrapper.le("price", max);
            }
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}