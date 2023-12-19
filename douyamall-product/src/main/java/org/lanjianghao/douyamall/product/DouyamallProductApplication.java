package org.lanjianghao.douyamall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;

//@MapperScan("org.lanjianghao.douyamall.product.dao")

@ImportResource(locations = {"classpath:springCacheMybatisPlusConfig.xml"})
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.lanjianghao.douyamall.product.feign")
public class DouyamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouyamallProductApplication.class, args);
    }

}
