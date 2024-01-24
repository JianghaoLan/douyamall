package org.lanjianghao.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSecKillOrderTo {
    private String orderSn;
    private Long memberId;
    private Integer num;

    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
}
