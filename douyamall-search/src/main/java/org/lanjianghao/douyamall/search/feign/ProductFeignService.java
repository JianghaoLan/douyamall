package org.lanjianghao.douyamall.search.feign;

import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("douyamall-product")
public interface ProductFeignService {
    @PostMapping("/product/attr/list/attrNames")
    R getAttrNames(@RequestBody List<Long> attrIds);

    @PostMapping("/product/brand/list/brandName")
    R getBrandNames(@RequestBody List<Long> brandIds);
}
