package org.lanjianghao.douyamall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.lanjianghao.douyamall.member.feign")
public class DouyamallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouyamallMemberApplication.class, args);
    }

}
