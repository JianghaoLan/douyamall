package org.lanjianghao.douyamall.order.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class OrderPriceCheckFailedException extends ApplicationException {
    public OrderPriceCheckFailedException(String msg) {
        super(BizCodeEnum.ORDER_PRICE_CHECK_FAILED_EXCEPTION, msg);
    }

    public OrderPriceCheckFailedException() {
        super(BizCodeEnum.ORDER_PRICE_CHECK_FAILED_EXCEPTION);
    }
}
