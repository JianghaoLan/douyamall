package org.lanjianghao.douyamall.thirdparty.component;

import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.lanjianghao.douyamall.thirdparty.utils.HttpUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("sms")
@Data
public class SmsComponentProperties {

    private String host;
    private String path;
    private String templateId;
    private String appCode;
}
