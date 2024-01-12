package org.lanjianghao.douyamall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.vo.OrderConfirmVo;
import org.lanjianghao.douyamall.order.vo.SubmitOrderVo;

import java.util.Map;

/**
 * 订单
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:45:24
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo getConfirmOrder(MemberVo loginUser);

    OrderEntity submitOrder(MemberVo loginUser, SubmitOrderVo submit);

    Integer getOrderStatusByOrderSn(String orderSn);

    void closeOrder(OrderEntity order);
}

