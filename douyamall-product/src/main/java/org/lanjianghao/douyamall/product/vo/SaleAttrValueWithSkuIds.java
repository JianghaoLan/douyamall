package org.lanjianghao.douyamall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SaleAttrValueWithSkuIds {
    private String value;
    private List<Long> skuIds;
}
