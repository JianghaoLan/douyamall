package org.lanjianghao.douyamall.product.feign;

import org.lanjianghao.common.to.MemberPriceTo;
import org.lanjianghao.common.to.SkuFullReductionTo;
import org.lanjianghao.common.to.SkuLadderTo;
import org.lanjianghao.common.to.SpuBoundTo;
import org.lanjianghao.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("douyamall-coupon")
public interface CouponFeignService {
    @PostMapping ("coupon/spubounds/save")
    R saveSpuBound(@RequestBody SpuBoundTo skuBoundTo);

    @PostMapping("coupon/skufullreduction/save/batch")
    R saveSkuFullReductions(@RequestBody List<SkuFullReductionTo> skuFullReductionTos);

    @PostMapping("coupon/skuladder/save/batch")
    R saveSkuLadders(@RequestBody List<SkuLadderTo> skuLadderTos);

    @PostMapping("coupon/memberprice/save/batch")
    R saveMemberPrices(@RequestBody List<MemberPriceTo> memberPriceTos);
}
