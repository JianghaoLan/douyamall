package org.lanjianghao.douyamall.product.feign;

import org.lanjianghao.common.to.SkuHasStockTo;
import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("douyamall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/list/hasstock")
    R listHasStocksBySkuIds(@RequestBody List<Long> skuIds);
}
