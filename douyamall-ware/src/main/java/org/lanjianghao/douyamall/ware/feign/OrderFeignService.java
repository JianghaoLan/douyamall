package org.lanjianghao.douyamall.ware.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("douyamall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/ordersn/{orderSn}/status")
    R getOrderStatusByOrderSn(@PathVariable("orderSn") String orderSn);
}
