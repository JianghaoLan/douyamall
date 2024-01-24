package org.lanjianghao.douyamall.member.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class FeignConfig {
    private String getCookiesString(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        List<String> cookieStrings = Arrays.stream(cookies).map(
                cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.toList());
        return String.join("; ", cookieStrings);
    }

    @Bean
    RequestInterceptor cookieRequestInterceptor() {
        return template -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();

                // 通过request.getHeader("Cookie")获取的请求头有时候为空，导致远程服务器无法正确获取登录用户信息。
                // 猜测原因为框架问题
//                String cookie = request.getHeader("Cookie");
//                if (cookie == null) {
//                    log.debug("请求cookie为空");
//                }
                Cookie[] cookies = request.getCookies();
                if (cookies == null) {
                    log.debug("请求cookie为空");
                }

                template.header("Cookie", getCookiesString(cookies));
            }
        };
    }
}
