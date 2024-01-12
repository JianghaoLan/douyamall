package org.lanjianghao.douyamall.product.vo;

import lombok.Data;
import org.lanjianghao.douyamall.product.entity.SpuInfoEntity;

@Data
public class GetSpuInfoBySkuIdVo {
    private Long skuId;
    private SpuInfoEntity spuInfo;
}
