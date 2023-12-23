package org.lanjianghao.douyamall.thirdparty.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.thirdparty.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SmsComponent {

    @Autowired
    private SmsComponentProperties properties;

    @Autowired
    ObjectMapper objectMapper;

    public R sendSmsCode(String phone, String code, int expire) {
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + properties.getAppCode());
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.put("content", "code:" + code + ",expire_at:" + expire);
        bodys.put("template_id", properties.getTemplateId());
        bodys.put("phone_number", phone);

        try {
            HttpResponse response = HttpUtils.doPost(properties.getHost(), properties.getPath(),
                    method, headers, querys, bodys);
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("验证码发送失败");
                return R.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok();
    }
}
