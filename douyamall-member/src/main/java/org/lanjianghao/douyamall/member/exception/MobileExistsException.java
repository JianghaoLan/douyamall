package org.lanjianghao.douyamall.member.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class MobileExistsException extends ApplicationException {
    public MobileExistsException() { super(BizCodeEnum.MOBILE_EXISTS_EXCEPTION); }
}
