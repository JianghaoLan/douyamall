package org.lanjianghao.douyamall.member.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class UsernameExistsException extends ApplicationException {
    public UsernameExistsException() { super(BizCodeEnum.USERNAME_EXISTS_EXCEPTION); }
}
