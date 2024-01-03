package org.lanjianghao.douyamall.member.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class OAuth2RegisterVo {

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 第三方账号所属平台
     */
    @NotNull
    private Integer oauth2Platform;

    /**
     * 绑定第三方账号uid
     */
    @NotEmpty
    private String socialUid;

    /**
     * 第三方账号的访问令牌
     */
    @NotEmpty
    private String accessToken;

    /**
     * 访问令牌的过期时间
     */
    @NotNull
    private Long expiresIn;

}
