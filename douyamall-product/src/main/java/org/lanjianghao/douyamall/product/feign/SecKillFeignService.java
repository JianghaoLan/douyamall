package org.lanjianghao.douyamall.product.feign;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.product.feign.fallback.SecKillFeignServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "douyamall-seckill", fallback = SecKillFeignServiceFallback.class)
public interface SecKillFeignService {
    @GetMapping("/sku/{skuId}")
    R getSecKillInfo(@PathVariable("skuId") Long skuId);
}
