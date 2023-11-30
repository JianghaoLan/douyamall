package org.lanjianghao.douyamall.product.feign;

import org.lanjianghao.common.to.SkuEsModel;
import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("douyamall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product/all")
    R saveProducts(@RequestBody List<SkuEsModel> productEntities);
}
