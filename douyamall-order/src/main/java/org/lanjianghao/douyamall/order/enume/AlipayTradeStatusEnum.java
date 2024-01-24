package org.lanjianghao.douyamall.order.enume;

public enum AlipayTradeStatusEnum {
    WAIT_BUYER_PAY("WAIT_BUYER_PAY"),
    TRADE_CLOSED("TRADE_CLOSED"),
    TRADE_SUCCESS("TRADE_SUCCESS"),
    TRADE_FINISHED("TRADE_FINISHED");

    private final String str;

    AlipayTradeStatusEnum(String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }
}
