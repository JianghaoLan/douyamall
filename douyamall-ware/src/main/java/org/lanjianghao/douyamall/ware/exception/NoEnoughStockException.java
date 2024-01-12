package org.lanjianghao.douyamall.ware.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class NoEnoughStockException extends ApplicationException {
    private final Long skuId;

    public NoEnoughStockException(String msg, Long skuId) {
        super(BizCodeEnum.NO_ENOUGH_STOCK_EXCEPTION, msg);
        this.skuId = skuId;
    }

    public Long getSkuId() {
        return skuId;
    }
}
