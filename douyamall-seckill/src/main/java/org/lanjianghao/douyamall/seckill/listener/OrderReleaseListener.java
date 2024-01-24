package org.lanjianghao.douyamall.seckill.listener;

import com.rabbitmq.client.Channel;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.douyamall.seckill.constant.SecKillConstant;
import org.lanjianghao.douyamall.seckill.service.SecKillService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = SecKillConstant.MQ_SEC_KILL_RELEASE_STOCK_QUEUE_NAME)
public class OrderReleaseListener {

    @Autowired
    SecKillService secKillService;

    @RabbitHandler
    public void releaseStockHandler(CreateSecKillOrderTo createSecKillOrderTo, Message message, Channel channel) {
        try {
            secKillService.releaseStockForSecKillOrder(createSecKillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ignored) {}
        }
    }
}
