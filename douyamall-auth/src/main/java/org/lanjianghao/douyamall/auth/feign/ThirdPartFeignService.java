package org.lanjianghao.douyamall.auth.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("douyamall-third-party")
public interface ThirdPartFeignService {
    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code,
               @RequestParam("expire") int expire);
}
