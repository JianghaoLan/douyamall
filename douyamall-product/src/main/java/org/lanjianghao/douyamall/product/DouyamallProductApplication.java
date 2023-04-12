package org.lanjianghao.douyamall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.lanjianghao.douyamall.product.dao")
@SpringBootApplication
public class DouyamallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouyamallProductApplication.class, args);
    }

}
