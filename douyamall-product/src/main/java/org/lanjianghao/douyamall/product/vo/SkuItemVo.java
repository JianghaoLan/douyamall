package org.lanjianghao.douyamall.product.vo;

import lombok.Data;
import lombok.ToString;
import org.lanjianghao.douyamall.product.entity.SkuImagesEntity;
import org.lanjianghao.douyamall.product.entity.SkuInfoEntity;
import org.lanjianghao.douyamall.product.entity.SpuInfoDescEntity;

import java.util.List;

@ToString
@Data
public class SkuItemVo {
    SkuInfoEntity info;

    Boolean hasStock = true;

    List<SkuImagesEntity> images;

    List<SkuSaleAttr> saleAttrs;

    SpuInfoDescEntity desc;

    List<SpuBaseAttrGroup> attrGroups;

    SecKillSkuVo secKillInfo;

}
