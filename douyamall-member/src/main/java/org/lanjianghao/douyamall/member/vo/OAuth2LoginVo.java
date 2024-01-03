package org.lanjianghao.douyamall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class OAuth2LoginVo {
    @NotNull
    private Integer platform;

    @NotEmpty(message = "uid不能为空")
    private String uid;

    @NotEmpty(message = "access token不能为空")
    private String accessToken;

    @NotNull(message = "过期时间不能为空")
    private Long expiresIn;
}
