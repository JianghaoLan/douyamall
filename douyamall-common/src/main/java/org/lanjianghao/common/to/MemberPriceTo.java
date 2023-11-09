package org.lanjianghao.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPriceTo {
    private Long skuId;
    private Long id;
    private String name;
    private BigDecimal price;
}
