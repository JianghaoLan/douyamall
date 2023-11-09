package org.lanjianghao.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuLadderTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
}
