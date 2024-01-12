package org.lanjianghao.douyamall.ware.listener;

import com.rabbitmq.client.Channel;
import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.common.to.WareOrderTaskTo;
import org.lanjianghao.douyamall.ware.service.WareSkuService;
import org.lanjianghao.common.to.OrderTo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = WareConstant.MQ_STOCK_RELEASE_STOCK_QUEUE_NAME)
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockReleaseMessage(WareOrderTaskTo wareOrderTaskTo, Message message, Channel channel) {
        try {
            wareSkuService.releaseStockByOrderTaskId(wareOrderTaskTo.getTaskId());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ignored) {}
        }
    }

    @RabbitHandler
    public void handleOrderRelease(OrderTo order, Message message, Channel channel) {
        try {
            wareSkuService.releaseStockByOrderSn(order.getOrderSn());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ignored) {}
        }
    }
}
