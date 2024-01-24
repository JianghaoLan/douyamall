package org.lanjianghao.douyamall.order.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.order.dao.OrderItemDao;
import org.lanjianghao.douyamall.order.entity.OrderItemEntity;
import org.lanjianghao.douyamall.order.service.OrderItemService;


@Slf4j
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderItemEntity getOneByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
    }

    @Override
    public List<OrderItemEntity> listByOrderSn(String orderSn) {
        return this.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
    }

}