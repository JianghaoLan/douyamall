package org.lanjianghao.douyamall.auth.to;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public class WeiboUserTo {
    //https://open.weibo.com/wiki/2/users/show
    private String id;
    private String screenName;
    private String name;
    private Integer province;
    private Integer city;
    private String location;
    private String description;
    private String url;
    private String profileImageUrl;
    private String profileUrl;
    private String domain;
    private String gender;
    //...
}
