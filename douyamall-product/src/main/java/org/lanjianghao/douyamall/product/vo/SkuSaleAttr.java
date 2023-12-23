package org.lanjianghao.douyamall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SkuSaleAttr {
    private Long attrId;
    private String attrName;
    private List<SaleAttrValueWithSkuIds> attrValues;
}
