package org.lanjianghao.douyamall.order.to;

import lombok.Data;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
