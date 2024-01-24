package org.lanjianghao.douyamall.member.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("douyamall-order")
public interface OrderFeignService {
    @PostMapping("/order/order/list/memberOrders")
    R listMemberOrders(@RequestBody Map<String, Object> params);
}
