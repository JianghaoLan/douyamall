package org.lanjianghao.douyamall.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AlipayNotifyVo {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    private String charset;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtPayment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date notifyTime;
    private String subject;
    private String sign;
    private String buyerId;
    private String body;
    private BigDecimal invoiceAmount;
    private String version;
    private String fundBillList;
    private String notifyType;
    private String outTradeNo;
    private BigDecimal totalAmount;
    private String tradeStatus;
    private String tradeNo;
    private String authAppId;
    private BigDecimal receiptAmount;
    private BigDecimal pointAmount;
    private BigDecimal buyerPayAmount;
    private String appId;
    private String signType;
    private String sellerId;
}
