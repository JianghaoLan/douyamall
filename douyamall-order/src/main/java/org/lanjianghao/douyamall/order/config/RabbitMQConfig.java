package org.lanjianghao.douyamall.order.config;

import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
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
    public Exchange orderEventExchange() {
        return ExchangeBuilder.topicExchange(OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return QueueBuilder.durable(OrderConstant.MQ_ORDER_RELEASE_ORDER_QUEUE_NAME).build();
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(OrderConstant.MQ_ORDER_DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", OrderConstant.MQ_ORDER_DELAY_QUEUE_DEAD_ROUTING_KEY)
                .withArgument("x-message-ttl", OrderConstant.ORDER_RELEASE_DELAY)
                .build();
    }

    @Bean
    public Binding orderReleaseOrderBinding(Queue orderReleaseOrderQueue, Exchange orderEventExchange) {
        return BindingBuilder.bind(orderReleaseOrderQueue)
                .to(orderEventExchange)
                .with(OrderConstant.MQ_ORDER_RELEASE_ORDER_BINDING_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Binding orderCreateOrderBinding(Queue orderDelayQueue, Exchange orderEventExchange) {
        return BindingBuilder.bind(orderDelayQueue)
                .to(orderEventExchange)
                .with(OrderConstant.MQ_ORDER_CREATE_ORDER_BINDING_ROUTING_KEY)
                .noargs();
    }

    /**
     * 和库存释放延时队列进行绑定
     */
    @Bean
    public Binding orderReleaseOtherBinding(Exchange orderEventExchange) {
        return BindingBuilder.bind(new Queue(WareConstant.MQ_STOCK_RELEASE_STOCK_QUEUE_NAME))
                .to(orderEventExchange)
                .with(OrderConstant.MQ_ORDER_RELEASE_OTHER_BINDING_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Queue orderSecKillOrderQueue() {
        return QueueBuilder.durable(OrderConstant.MQ_ORDER_SEC_KILL_ORDER_QUEUE_NAME).build();
    }

    @Bean
    public Binding orderSecKillOrderBinding(Queue orderSecKillOrderQueue, Exchange orderEventExchange) {
        return BindingBuilder.bind(orderSecKillOrderQueue)
                .to(orderEventExchange)
                .with(OrderConstant.MQ_ORDER_SEC_KILL_ORDER_BINDING_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Queue orderSecKillDelayQueue() {
        return QueueBuilder.durable(OrderConstant.MQ_ORDER_SEC_KILL_DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", OrderConstant.MQ_ORDER_EVENT_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", OrderConstant.MQ_ORDER_SEC_KILL_RELEASE_ORDER_BINDING_ROUTING_KEY)
                .withArgument("x-message-ttl", OrderConstant.ORDER_SEC_KILL_RELEASE_DELAY)
                .build();
    }

    @Bean
    public Queue orderSecKillReleaseOrderQueue() {
        return QueueBuilder.durable(OrderConstant.MQ_ORDER_SEC_KILL_RELEASE_ORDER_QUEUE_NAME).build();
    }

    @Bean
    public Binding orderSecKillReleaseOrderBinding(Queue orderSecKillReleaseOrderQueue, Exchange orderEventExchange) {
        return BindingBuilder.bind(orderSecKillReleaseOrderQueue)
                .to(orderEventExchange)
                .with(OrderConstant.MQ_ORDER_SEC_KILL_RELEASE_ORDER_BINDING_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Binding orderSecKillCreateOrderBinding(Queue orderSecKillDelayQueue, Exchange orderEventExchange) {
        return BindingBuilder.bind(orderSecKillDelayQueue)
                .to(orderEventExchange)
                .with(OrderConstant.MQ_ORDER_SEC_KILL_CREATE_ORDER_BINDING_ROUTING_KEY)
                .noargs();
    }

    @Bean
    public Binding secKillReleaseStockBinding(Exchange orderEventExchange) {
        return BindingBuilder.bind(new TopicExchange(OrderConstant.MQ_SEC_KILL_EVENT_EXCHANGE_NAME))
                .to(orderEventExchange)
                .with(OrderConstant.MQ_SEC_KILL_RELEASE_STOCK_BINDING_ROUTING_KEY)
                .noargs();
    }
}
