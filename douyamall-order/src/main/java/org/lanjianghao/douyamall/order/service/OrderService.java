package org.lanjianghao.douyamall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.entity.PaymentInfoEntity;
import org.lanjianghao.douyamall.order.exception.CloseOrderFailedException;
import org.lanjianghao.douyamall.order.vo.AlipayPayVo;
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

    void closeOrderByOrderSn(String orderSn) throws CloseOrderFailedException;

    AlipayPayVo getAlipayPayVo(String orderSn);

    OrderEntity getOrderByOrderSn(String orderSn);

    PageUtils queryMemberOrdersPage(Long memberId, Map<String, Object> params);

    void handlePayment(PaymentInfoEntity payment);

    void createSecKillOrder(CreateSecKillOrderTo createOrderTo);

    void closeSecKillOrder(CreateSecKillOrderTo createSecKillOrderTo) throws CloseOrderFailedException;
}

