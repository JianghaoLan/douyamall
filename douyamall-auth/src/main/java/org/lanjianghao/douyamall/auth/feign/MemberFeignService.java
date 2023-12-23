package org.lanjianghao.douyamall.auth.feign;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("douyamall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(@RequestBody RegisterVo vo);
}
