package org.lanjianghao.douyamall.seckill.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("douyamall-product")
public interface ProductFeignService {
    @PostMapping("/product/skuinfo/infos")
    R getSkuInfosBySkuIds(@RequestBody List<Long> skuIds);
}
