package org.lanjianghao.douyamall.auth.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.common.exception.BizCodeEnum;
import org.lanjianghao.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentinelConfig {

    @Autowired
    ObjectMapper objectMapper;

    SentinelConfig(SentinelWebMvcConfig sentinelWebMvcConfig) {
        sentinelWebMvcConfig.setBlockExceptionHandler((request, response, e) -> {
            R r = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMessage());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(r));
        });
    }
}
