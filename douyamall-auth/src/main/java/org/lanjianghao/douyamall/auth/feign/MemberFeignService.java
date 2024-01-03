package org.lanjianghao.douyamall.auth.feign;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.auth.vo.LoginVo;
import org.lanjianghao.douyamall.auth.vo.OAuth2LoginVo;
import org.lanjianghao.douyamall.auth.vo.OAuth2RegisterVo;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("douyamall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(@RequestBody RegisterVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody LoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oAuth2Login(@RequestBody OAuth2LoginVo vo);

    @PostMapping("/member/member/oauth2/register")
    R oAuth2Register(@RequestBody OAuth2RegisterVo vo);
}
