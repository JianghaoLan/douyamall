package org.lanjianghao.douyamall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,
        scanBasePackages = { "org.lanjianghao.douyamall.search", "org.lanjianghao.common"})
public class DouyamallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouyamallSearchApplication.class, args);
    }

}
