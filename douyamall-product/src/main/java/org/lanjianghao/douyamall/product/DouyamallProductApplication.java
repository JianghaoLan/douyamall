package org.lanjianghao.douyamall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@MapperScan("org.lanjianghao.douyamall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.lanjianghao.douyamall.product.feign")
public class DouyamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouyamallProductApplication.class, args);
    }

}
