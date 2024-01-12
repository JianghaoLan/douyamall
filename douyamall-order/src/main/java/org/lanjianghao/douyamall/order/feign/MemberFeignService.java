package org.lanjianghao.douyamall.order.feign;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.order.vo.MemberReceiveAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("douyamall-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/list")
    List<MemberReceiveAddressVo> listUserReceiveAddresses();

    @GetMapping("/member/member/integration")
    R integration();
}
