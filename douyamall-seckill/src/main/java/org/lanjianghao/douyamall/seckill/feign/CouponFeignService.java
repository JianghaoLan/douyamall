package org.lanjianghao.douyamall.seckill.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("douyamall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/upcoming")
    R getUpcomingSessions(@RequestParam("days") Long days);
}
