package org.lanjianghao.douyamall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = OrderConstant.MQ_ORDER_RELEASE_ORDER_QUEUE_NAME)
@Service
@Slf4j
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity order, Message message, Channel channel) {
//        log.info("接收到消息：{}，类型：{}", order, msg.getClass().getName());
        try {
            orderService.closeOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ignored) {}
        }
    }
}
