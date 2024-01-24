package org.lanjianghao.douyamall.seckill.config;

import org.lanjianghao.douyamall.seckill.constant.SecKillConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Exchange secKillEventExchange() {
        return ExchangeBuilder.topicExchange(SecKillConstant.MQ_SEC_KILL_EVENT_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue secKillReleaseStockQueue() {
        return QueueBuilder.durable(SecKillConstant.MQ_SEC_KILL_RELEASE_STOCK_QUEUE_NAME).build();
    }

    @Bean
    public Binding orderSecKillReleaseStockBinding(Queue secKillReleaseStockQueue, Exchange secKillEventExchange) {
        return BindingBuilder.bind(secKillReleaseStockQueue)
                .to(secKillEventExchange)
                .with(SecKillConstant.MQ_SEC_KILL_RELEASE_OTHER_BINDING_ROUTING_KEY)
                .noargs();
    }
}
