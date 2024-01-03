package org.lanjianghao.douyamall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class OAuth2LoginVo {
    private Integer platform;

    private String uid;

    private String accessToken;

    private Long expiresIn;
}
