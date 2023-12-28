package org.lanjianghao.douyamall.cart.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class SkuNotExistsException extends ApplicationException {
    public SkuNotExistsException() { super(BizCodeEnum.SKU_NOT_EXISTS_EXCEPTION); }
}
