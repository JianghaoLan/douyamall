package org.lanjianghao.douyamall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
public class DouyamallCouponApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DouyamallCouponApplication.class, args);
//        while (true) {
////            String userName = applicationContext.getEnvironment().getProperty("coupon.user.name");
////            String userAge = applicationContext.getEnvironment().getProperty("coupon.user.age");
//            String userName = applicationContext.getEnvironment().getProperty("user.name");
//            String userAge = applicationContext.getEnvironment().getProperty("user.age");
//            System.err.println("user name :"+userName+"; age: "+userAge);
//            TimeUnit.SECONDS.sleep(1);
//        }
    }

}
