package org.lanjianghao.douyamall.auth.service.impl;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.feign.MemberFeignService;
import org.lanjianghao.douyamall.auth.service.LoginService;
import org.lanjianghao.douyamall.auth.service.SmsCodeService;
import org.lanjianghao.douyamall.auth.vo.LoginVo;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    SmsCodeService smsCodeService;

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public R register(RegisterVo vo) {
        smsCodeService.verifySmsCode(vo.getPhone(), vo.getCode());

        return memberFeignService.register(vo);
    }

    @Override
    public R login(LoginVo vo) {
        return memberFeignService.login(vo);
    }


}
