package org.lanjianghao.douyamall.auth.to;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WeiboAccessTokenRespTo {
    private String accessToken;
    private Long expiresIn;
    private String uid;
    private String isRealName;
}
