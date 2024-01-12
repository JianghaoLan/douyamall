package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.douyamall.product.entity.*;
import org.lanjianghao.douyamall.product.service.*;
import org.lanjianghao.douyamall.product.to.SkuSpuIdTo;
import org.lanjianghao.douyamall.product.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.SkuInfoDao;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    ThreadPoolExecutor executor;

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

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVo getItem(Long skuId) {
        SkuItemVo item = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //set sku info
            SkuInfoEntity skuInfo = this.getById(skuId);
            item.setInfo(skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> descFuture = skuInfoFuture.thenAcceptAsync(info -> {
            //set desc
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(info.getSpuId());
            item.setDesc(spuInfoDesc);
        }, executor);

        CompletableFuture<Void> saleAttrsFuture = skuInfoFuture.thenAcceptAsync(info -> {
            //set sale attrs
            List<SkuSaleAttr> saleAttrs = skuSaleAttrValueService.listSaleAttrBySpuId(info.getSpuId());
            item.setSaleAttrs(saleAttrs);
        }, executor);

        CompletableFuture<Void> attrGroupsFuture = skuInfoFuture.thenAcceptAsync(info -> {
            //set attr groups
            List<SpuBaseAttrGroup> baseAttrGroups = attrGroupService.listAttrGroupBySpuId(info.getSpuId());
            item.setAttrGroups(baseAttrGroups);
        }, executor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImages = skuImagesService.listBySkuId(skuId);
            item.setImages(skuImages);
        }, executor);

        try {
            CompletableFuture.allOf(descFuture, saleAttrsFuture, attrGroupsFuture, imagesFuture).get();
        } catch (InterruptedException | ExecutionException ignored) { }

        return item;
    }

    @Override
    public List<SkuPriceVo> listPricesBySkuIds(List<Long> skuIds) {
        return this.baseMapper.selectPricesBySkuIds(skuIds);
    }

//    @Override
//    public Long getSpuIdBySkuId(Long skuId) {
//        return this.baseMapper.getSpuIdBySkuId(skuId);
//    }

    @Override
    public List<SkuSpuIdTo> getSpuIdsBySkuIds(List<Long> skuIds) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return null;
        }
        return this.baseMapper.getSpuIdsBySkuIds(skuIds);
    }
}