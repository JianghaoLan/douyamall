package org.lanjianghao.douyamall.auth.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class OAuth2LoginFailedException extends ApplicationException {
    public OAuth2LoginFailedException() { super(BizCodeEnum.OAUTH2_LOGIN_FAILED_EXCEPTION); }
}
