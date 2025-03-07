package org.lanjianghao.douyamall.seckill.config;

import org.lanjianghao.douyamall.seckill.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DouyamallSecKillWebConfig implements WebMvcConfigurer {

    @Autowired
    AuthInterceptor authInterceptor;

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/kill");
    }
}
