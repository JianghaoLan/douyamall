package org.lanjianghao.douyamall.ware.config;

import org.lanjianghao.common.constant.WareConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.lanjianghao.common.constant.WareConstant.STOCK_RELEASE_DELAY;

@Configuration
public class RabbitMQConfig {
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange() {
        return ExchangeBuilder.topicExchange(WareConstant.MQ_STOCK_EVENT_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return QueueBuilder.durable(WareConstant.MQ_STOCK_RELEASE_STOCK_QUEUE_NAME).build();
    }

    @Bean
    public Queue stockDelayQueue() {
        return QueueBuilder.durable(WareConstant.MQ_STOCK_DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", WareConstant.MQ_STOCK_EVENT_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", WareConstant.MQ_STOCK_DELAY_DEAD_ROUTING_KEY)
                .withArgument("x-message-ttl", STOCK_RELEASE_DELAY)
                .build();
    }

    @Bean
    public Binding stockReleaseBinding(Queue stockReleaseStockQueue, Exchange stockEventExchange) {
        return BindingBuilder.bind(stockReleaseStockQueue)
                .to(stockEventExchange)
                .with(WareConstant.MQ_STOCK_RELEASE_BINDING_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Binding stockLockedBinding(Queue stockDelayQueue, Exchange stockEventExchange) {
        return BindingBuilder.bind(stockDelayQueue)
                .to(stockEventExchange)
                .with(WareConstant.MQ_STOCK_LOCKED_BINDING_ROUTING_KEY)
                .noargs();
    }
}
