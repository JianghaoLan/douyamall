package org.lanjianghao.douyamall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuPriceVo {
    private Long skuId;
    private BigDecimal price;
}
