package org.lanjianghao.douyamall.order.service.impl;

import com.alipay.api.AlipayApiException;
import org.lanjianghao.douyamall.order.component.AlipayTemplate;
import org.lanjianghao.douyamall.order.exception.AlipayPayFailedException;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.lanjianghao.douyamall.order.service.PayService;
import org.lanjianghao.douyamall.order.vo.AlipayPayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @Override
    public String alipay(String orderSn) {
        AlipayPayVo payVo = orderService.getAlipayPayVo(orderSn);

        try {
            return alipayTemplate.pay(payVo);
        } catch (AlipayApiException e) {
            throw new AlipayPayFailedException(e);
        }
    }
}
