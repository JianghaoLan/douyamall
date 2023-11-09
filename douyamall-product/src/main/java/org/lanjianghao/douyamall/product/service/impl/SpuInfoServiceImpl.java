package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.common.to.MemberPriceTo;
import org.lanjianghao.common.to.SkuFullReductionTo;
import org.lanjianghao.common.to.SkuLadderTo;
import org.lanjianghao.common.to.SpuBoundTo;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.product.entity.*;
import org.lanjianghao.douyamall.product.feign.CouponFeignService;
import org.lanjianghao.douyamall.product.service.*;
import org.lanjianghao.douyamall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    //TODO 高级部分完善
    @Transactional
    @Override
    public void saveSpu(SpuSaveVo spuSaveVo) {
        //保存SpuInfo
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.save(spuInfo);
        Long spuId = spuInfo.getId();

        //保存SpuInfoDesc
        List<String> decriptList = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuId);
        spuInfoDesc.setDecript(String.join(",", decriptList));
        spuInfoDescService.save(spuInfoDesc);

        //保存images
        List<String> imageUrlList = spuSaveVo.getImages();
        List<SpuImagesEntity> spuImageList = imageUrlList.stream().map(url -> {
            SpuImagesEntity spuImage = new SpuImagesEntity();
            spuImage.setSpuId(spuId);
            spuImage.setImgUrl(url);
            return spuImage;
        }).collect(Collectors.toList());
        spuImagesService.saveBatch(spuImageList);

        //保存ProductAttrValue
        List<BaseAttr> baseAttrList = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueList = baseAttrList.stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValue = new ProductAttrValueEntity();
            productAttrValue.setSpuId(spuId);
            productAttrValue.setAttrId(baseAttr.getAttrId());
            AttrEntity attr = attrService.getById(baseAttr.getAttrId());
            productAttrValue.setAttrName(attr.getAttrName());
            productAttrValue.setAttrValue(baseAttr.getAttrValues());
            productAttrValue.setQuickShow(baseAttr.getShowDesc());
            return productAttrValue;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueList);

        //远程调用保存SkuBounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuId);
        R saveSpuBoundR = couponFeignService.saveSpuBound(spuBoundTo);
        if (saveSpuBoundR.getCode() != 0) {
            log.error("远程保存SPU积分信息失败");
        }

        //保存SkuInfo
        List<Sku> skuList = spuSaveVo.getSkus();
        if (skuList == null) {
            skuList = Collections.emptyList();
        }
        List<SkuInfoEntity> skuInfoList = skuList.stream().map(sku -> {
            String defaultImg = null;
            for (Image image : sku.getImages()) {
                if (image.getDefaultImg() == 1) {
                    defaultImg = image.getImgUrl();
                    break;
                }
            }

            SkuInfoEntity skuInfo = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, skuInfo);
            skuInfo.setSpuId(spuId);
            skuInfo.setCatalogId(spuInfo.getCatalogId());
            skuInfo.setBrandId(spuInfo.getBrandId());
            skuInfo.setSkuDefaultImg(defaultImg);
            skuInfo.setSaleCount(0L);
            return skuInfo;
        }).collect(Collectors.toList());
        skuInfoService.saveBatch(skuInfoList);

        //保存SkuImages
        List<SkuImagesEntity> skuImageList = new ArrayList<>();
        //保存SkuSaleAttrValue
        List<SkuSaleAttrValueEntity> skuSaleAttrValueList = new ArrayList<>();
        //保存满减信息SkuFullReduction
        List<SkuFullReductionTo> skuFullReductionToList = new ArrayList<>();
        //保存多件折扣信息SkuLadder
        List<SkuLadderTo> skuLadderToList = new ArrayList<>();
        //保存会员价信息MemberPrice
        List<MemberPriceTo> memberPriceToList = new ArrayList<>();
        for (int i = 0; i < skuList.size(); i++) {
            Sku sku = skuList.get(i);
            Long skuId = skuInfoList.get(i).getSkuId();

            List<Image> imageList = sku.getImages();
            imageList.forEach(image -> {
                if (!StringUtils.hasLength(image.getImgUrl())) {
                    return;
                }
                SkuImagesEntity skuImage = new SkuImagesEntity();
                BeanUtils.copyProperties(image, skuImage);
                skuImage.setSkuId(skuId);
                skuImageList.add(skuImage);
            });

            List<Attr> attrList = sku.getAttr();
            attrList.forEach(attr -> {
                SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttrValue);
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValueList.add(skuSaleAttrValue);
            });

            if (sku.getFullPrice().compareTo(new BigDecimal("0")) > 0
                    && sku.getReducePrice().compareTo(new BigDecimal("0")) > 0) {
                SkuFullReductionTo skuFullReductionTo = new SkuFullReductionTo();
                BeanUtils.copyProperties(sku, skuFullReductionTo);
                skuFullReductionTo.setSkuId(skuId);
                skuFullReductionToList.add(skuFullReductionTo);
            }

            if (sku.getFullCount() > 0 && sku.getDiscount().compareTo(new BigDecimal("0")) > 0) {
                SkuLadderTo skuLadderTo = new SkuLadderTo();
                BeanUtils.copyProperties(sku, skuLadderTo);
                skuLadderTo.setSkuId(skuId);
                skuLadderToList.add(skuLadderTo);
            }

            sku.getMemberPrice().forEach(memberPrice -> {
                if (memberPrice.getPrice().compareTo(new BigDecimal("0")) <= 0) {
                    return;
                }
                MemberPriceTo memberPriceTo = new MemberPriceTo();
                BeanUtils.copyProperties(memberPrice, memberPriceTo);
                memberPriceTo.setSkuId(skuId);
                memberPriceToList.add(memberPriceTo);
            });
        }
        skuImagesService.saveBatch(skuImageList);
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
        R saveSkuFullReductionsR = couponFeignService.saveSkuFullReductions(skuFullReductionToList);
        if (saveSkuFullReductionsR.getCode() != 0) {
            log.error("远程保存SKU满减信息失败");
        }
        R saveSkuLaddersR = couponFeignService.saveSkuLadders(skuLadderToList);
        if (saveSkuLaddersR.getCode() != 0) {
            log.error("远程保存SKU多件折扣信息失败");
        }
        R saveMemberPricesR = couponFeignService.saveMemberPrices(memberPriceToList);
        if (saveMemberPricesR.getCode() != 0) {
            log.error("远程保存会员价信息失败");
        }
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        /**
         *    key: '华为',//检索关键字
         *    catelogId: 6,//三级分类id
         *    brandId: 1,//品牌id
         *    status: 0,//商品状态
         */
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String)params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and(wrapper -> wrapper.eq("id", key).or().like("spu_name", key)
                    .or().like("spu_description", key));
        }

        String catelogId = (String)params.get("catelogId");
        if (StringUtils.hasLength(catelogId) && Integer.parseInt(catelogId) != 0) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String)params.get("brandId");
        if (StringUtils.hasLength(brandId) && Integer.parseInt(brandId) != 0) {
            queryWrapper.eq("brand_id", brandId);
        }

        String status = (String)params.get("status");
        if (StringUtils.hasLength(status)) {
            queryWrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
}