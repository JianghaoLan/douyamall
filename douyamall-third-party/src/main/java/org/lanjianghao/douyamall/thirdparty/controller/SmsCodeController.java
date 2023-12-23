package org.lanjianghao.douyamall.thirdparty.controller;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsCodeController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用
     * @param phone
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code,
                      @RequestParam("expire") int expire) {
        return smsComponent.sendSmsCode(phone, code, expire);
    }
}
