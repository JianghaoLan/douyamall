package org.lanjianghao.douyamall.order.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = OrderConstant.MQ_ORDER_SEC_KILL_ORDER_QUEUE_NAME)
@Component
@Slf4j
public class SecKillListener {
    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(CreateSecKillOrderTo createOrderTo, Message message, Channel channel) {
        log.debug("准备创建秒杀单");
        try {
            orderService.createSecKillOrder(createOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ignored) {
            }
        }
    }
}
