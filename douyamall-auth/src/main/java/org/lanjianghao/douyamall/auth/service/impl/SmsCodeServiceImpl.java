package org.lanjianghao.douyamall.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.exception.CodeVerificationFailedException;
import org.lanjianghao.douyamall.auth.exception.SmsCodeSendIntervalException;
import org.lanjianghao.douyamall.auth.exception.SmsSendCodeException;
import org.lanjianghao.douyamall.auth.feign.ThirdPartFeignService;
import org.lanjianghao.douyamall.auth.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    Random random;

    SmsCodeServiceImpl() {
        random = new Random();
    }

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private String getRedisKey(String phone) {
        return AuthConstant.SMS_REDIS_KEY_PREFIX + phone;
    }

    private String generateCode() {
        return String.format("%04d", random.nextInt(10000));
    }

    private boolean validateInterval(String phone) {
        String value = redisTemplate.opsForValue().get(getRedisKey(phone));
        if (value == null) {
            return true;
        }

        long lastSendTime;
        try {
            lastSendTime = Long.parseLong(value.split("_", 2)[1]);
        } catch (IndexOutOfBoundsException e) {
            return true;
        }

        return System.currentTimeMillis() - lastSendTime > AuthConstant.SMS_MIN_SEND_INTERVAL * 60 * 1000;
    }

    @Override
    public void sendSmsCode(String phone) throws SmsCodeSendIntervalException, SmsSendCodeException {
        //TODO 接口防刷
        if (!validateInterval(phone)) {
            throw new SmsCodeSendIntervalException();
        }

        String code = generateCode();
        String value = code + "_" + System.currentTimeMillis();
        R r = thirdPartFeignService.sendCode(phone, code, AuthConstant.SMS_EXPIRE);
        if (r.getCode() != 0) {
//            throw new SmsSendCodeException();
            log.error("验证码发送失败");
        }
        redisTemplate.opsForValue().set(getRedisKey(phone), value, AuthConstant.SMS_EXPIRE, TimeUnit.MINUTES);
    }

    @Override
    public void verifySmsCode(String phone, String code) throws CodeVerificationFailedException {
        String key = getRedisKey(phone);
        String value = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasLength(value)) {
            throw new CodeVerificationFailedException("验证码校验不通过");
        }
        String[] split = value.split("_", 2);
        if (!code.equals(split[0])) {
            throw new CodeVerificationFailedException("验证码校验不通过");
        }
        redisTemplate.delete(key);
    }
}
