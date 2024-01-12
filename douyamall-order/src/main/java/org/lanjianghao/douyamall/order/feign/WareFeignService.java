package org.lanjianghao.douyamall.order.feign;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("douyamall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/list/hasstock")
    R listHasStocksBySkuIds(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
    R lockStockForOrder(@RequestBody WareSkuLockVo wareSkuLockVo);
}
