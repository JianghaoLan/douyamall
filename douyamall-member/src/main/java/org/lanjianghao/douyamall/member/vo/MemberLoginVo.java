package org.lanjianghao.douyamall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MemberLoginVo {

    private String account;

    private String password;
}
