package org.lanjianghao.common.config;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;

@Configuration
public class ValidationConfig {
    @Bean
    public static LocalValidatorFactoryBean validator(ApplicationContext applicationContext) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(applicationContext);
        factoryBean.setMessageInterpolator(new ResourceBundleMessageInterpolator(
//                        new PlatformResourceBundleLocator("common.ValidationMessages")
                        new AggregateResourceBundleLocator(
                                Arrays.asList(
                                        "ValidationMessages",
                                        "common.ValidationMessages"
                                )
                        )
                )
        );
        return factoryBean;
    }
}
