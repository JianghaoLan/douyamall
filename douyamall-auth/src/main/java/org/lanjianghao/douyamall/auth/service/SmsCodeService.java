package org.lanjianghao.douyamall.auth.service;

import org.lanjianghao.douyamall.auth.exception.CodeVerificationFailedException;
import org.lanjianghao.douyamall.auth.exception.SmsCodeSendIntervalException;
import org.lanjianghao.douyamall.auth.exception.SmsSendCodeException;
import org.springframework.stereotype.Service;

public interface SmsCodeService {
    void sendSmsCode(String phone) throws SmsCodeSendIntervalException, SmsSendCodeException;

    void verifySmsCode(String phone, String code) throws CodeVerificationFailedException;
}
