package org.lanjianghao.douyamall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginVo {

    @NotBlank(message = "用户不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}
