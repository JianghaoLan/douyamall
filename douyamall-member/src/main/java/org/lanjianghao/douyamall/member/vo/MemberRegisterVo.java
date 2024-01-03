package org.lanjianghao.douyamall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MemberRegisterVo {

    @NotEmpty
    private String userName;

    @NotEmpty
    private String password;

    @NotEmpty
    private String phone;
}
