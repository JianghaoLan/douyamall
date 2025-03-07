package org.lanjianghao.douyamall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
//@MapperScan("org.lanjianghao.douyamall.ware.dao")
@EnableFeignClients(basePackages = "org.lanjianghao.douyamall.ware.feign")
public class DouyamallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouyamallWareApplication.class, args);
    }

}
