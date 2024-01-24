package org.lanjianghao.douyamall.order.vo;

import lombok.Data;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.entity.OrderItemEntity;

import java.util.List;

@Data
public class MemberOrderVo {
    private OrderEntity order;
    private List<OrderItemEntity> items;
}
