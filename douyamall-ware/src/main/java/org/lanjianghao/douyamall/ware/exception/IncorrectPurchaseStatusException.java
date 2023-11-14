package org.lanjianghao.douyamall.ware.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class IncorrectPurchaseStatusException extends ApplicationException {

    public IncorrectPurchaseStatusException() {
        super(BizCodeEnum.INCORRECT_PURCHASE_STATUS_EXCEPTION);
    }
}
