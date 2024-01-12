package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {

    private List<MemberReceiveAddressVo> addresses;

    private List<CartItemVo> items;

    private Integer integration;

    /**
     * 防重令牌
     */
    private String orderToken;

//    private BigDecimal totalPrice;

//    private BigDecimal payPrice;

    public BigDecimal getTotalPrice() {
        BigDecimal total = new BigDecimal(0);
        if (items != null) {
            for (CartItemVo item : items) {
                total = total.add(item.getTotalPrice());
            }
        }
        return total;
    }

    public int getTotalNum() {
        int count = 0;
        if (items != null) {
            for (CartItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }
}

