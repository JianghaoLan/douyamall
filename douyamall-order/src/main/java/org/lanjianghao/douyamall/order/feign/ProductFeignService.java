package org.lanjianghao.douyamall.order.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("douyamall-product")
public interface ProductFeignService {
    @PostMapping("/product/spuinfo/byskuids")
    R getSpuInfosBySkuIds(@RequestBody List<Long> skuIds);

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfoBySkuId(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/spuinfo/info/{id}")
    R getSpuInfoBySpuId(@PathVariable("id") Long id);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    R getSkuSaleAttrValueStrings(@PathVariable("skuId") Long skuId);
}
