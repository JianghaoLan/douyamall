package org.lanjianghao.douyamall.order.feign;


import org.lanjianghao.douyamall.order.vo.CartItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("douyamall-cart")
public interface CartFeignService {
    @GetMapping("/list/checked")
    List<CartItemVo> listUserCartCheckedItems();
}
