package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    private MemberReceiveAddressVo address;
    private BigDecimal fare;
}
