package org.lanjianghao.douyamall.ware.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class GetFareErrorException extends ApplicationException {
    public GetFareErrorException() {
        super(BizCodeEnum.GET_FARE_ERROR_EXCEPTION);
    }

    public GetFareErrorException(String msg) {
        super(BizCodeEnum.GET_FARE_ERROR_EXCEPTION, msg);
    }
}
