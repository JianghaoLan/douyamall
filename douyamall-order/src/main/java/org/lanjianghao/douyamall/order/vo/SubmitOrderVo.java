package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmitOrderVo {
    private Long addrId;
    private Integer payType;
    private String orderToken;   //防重令牌
    private BigDecimal payPrice;
    private String note;
}
