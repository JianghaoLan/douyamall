package org.lanjianghao.douyamall.auth.exception;

import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.common.exception.BizCodeEnum;

public class SmsCodeSendIntervalException extends ApplicationException {
    public SmsCodeSendIntervalException() {
        super(BizCodeEnum.SMS_CODE_SEND_INTERVAL_EXCEPTION);
    }
}
