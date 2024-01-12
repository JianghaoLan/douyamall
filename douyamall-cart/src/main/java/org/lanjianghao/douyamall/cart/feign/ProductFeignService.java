package org.lanjianghao.douyamall.cart.feign;


import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("douyamall-product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    R getSaleAttrValuesAsStringList(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/skuinfo/prices")
    R prices(@RequestBody List<Long> skuIds);
}
