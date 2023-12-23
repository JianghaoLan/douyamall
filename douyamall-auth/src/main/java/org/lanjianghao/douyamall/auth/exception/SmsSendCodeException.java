package org.lanjianghao.douyamall.auth.exception;

import org.lanjianghao.common.exception.ApplicationException;

import static org.lanjianghao.common.exception.BizCodeEnum.SMS_SEND_CODE_EXCEPTION;

public class SmsSendCodeException extends ApplicationException {
    public SmsSendCodeException() { super(SMS_SEND_CODE_EXCEPTION); }
}
