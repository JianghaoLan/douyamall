package org.lanjianghao.douyamall.order.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class OrderTokenVerifyFailedException extends ApplicationException {
    public OrderTokenVerifyFailedException(final String msg) {
        super(BizCodeEnum.ORDER_TOKEN_VERIFY_FAILED_EXCEPTION, msg);
    }

    OrderTokenVerifyFailedException() {
        super(BizCodeEnum.ORDER_TOKEN_VERIFY_FAILED_EXCEPTION);
    }
}
