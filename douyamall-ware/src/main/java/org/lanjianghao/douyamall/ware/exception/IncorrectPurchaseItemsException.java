package org.lanjianghao.douyamall.ware.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class IncorrectPurchaseItemsException extends ApplicationException {
    public IncorrectPurchaseItemsException() {
        super(BizCodeEnum.INCORRECT_PURCHASE_ITEMS_EXCEPTION);
    }
}
