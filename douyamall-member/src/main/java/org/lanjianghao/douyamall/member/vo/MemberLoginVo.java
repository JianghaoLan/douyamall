package org.lanjianghao.douyamall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class MemberLoginVo {

    @NotEmpty
    private String account;

    @NotEmpty
    private String password;
}
