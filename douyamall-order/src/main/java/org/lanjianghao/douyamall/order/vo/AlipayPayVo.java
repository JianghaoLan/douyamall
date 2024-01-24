package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

@Data
public class AlipayPayVo {
    /**
     * 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    String outTradeNo;
    /**
     * 付款金额，必填
     */
    String totalAmount;
    /**
     * 订单名称，必填
     */
    String subject;
    /**
     * 商品描述，可空
     */
    String body;
}
