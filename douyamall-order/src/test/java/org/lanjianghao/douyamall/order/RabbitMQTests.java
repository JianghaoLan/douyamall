package org.lanjianghao.douyamall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RabbitMQTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCreateExchange() {
        Exchange directExchange = ExchangeBuilder.directExchange("hello-java-exchange").durable(true).build();
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello-java-exchange");
    }

    @Test
    public void testCreateQueue() {
        Queue queue = QueueBuilder.durable("hello-java-queue").build();
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello-java-queue");
    }

    @Test
    public void testCreateBinding() {
        Binding binding = BindingBuilder
                .bind(QueueBuilder.durable("hello-java-queue").build())
                .to((Exchange) ExchangeBuilder.directExchange("hello-java-exchange").build())
                .with("hello.java")
                .noargs();
        amqpAdmin.declareBinding(binding);
        log.info("Binding创建成功");
    }

    @Test
    public void testSendMsg() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(2L);
        orderEntity.setBillHeader("哈哈哈");
        for (int i = 0; i < 5; i++) {
            rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity);
        }
        log.info("已发送消息");
    }
}
