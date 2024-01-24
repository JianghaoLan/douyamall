package org.lanjianghao.douyamall.order.listener;

import com.rabbitmq.client.Channel;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
import org.lanjianghao.douyamall.order.exception.CloseOrderFailedException;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = OrderConstant.MQ_ORDER_SEC_KILL_RELEASE_ORDER_QUEUE_NAME)
public class SecKillOrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void secKillOrderCloseListener(CreateSecKillOrderTo createSecKillOrderTo, Message message, Channel channel) {
        try {
            orderService.closeSecKillOrder(createSecKillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (CloseOrderFailedException | IOException e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ignored) {}
        }
    }
}
