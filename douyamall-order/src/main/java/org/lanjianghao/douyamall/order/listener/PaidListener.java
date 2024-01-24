package org.lanjianghao.douyamall.order.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.douyamall.order.component.AlipayTemplate;
import org.lanjianghao.douyamall.order.entity.PaymentInfoEntity;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.lanjianghao.douyamall.order.vo.AlipayNotifyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/paid")
public class PaidListener {

    @Autowired
    OrderService orderService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @PostMapping("/alipay")
    public String handleAlipayPaidNotify(@RequestParam Map<String, String> params) {
        boolean signVerified = alipayTemplate.checkSignature(params);
        if (!signVerified) {
            return "error";
        }

        AlipayNotifyVo alipayNotify = mapper.convertValue(params, AlipayNotifyVo.class);
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setAlipayTradeNo(alipayNotify.getTradeNo());
        paymentInfo.setOrderSn(alipayNotify.getOutTradeNo());
        paymentInfo.setSubject(alipayNotify.getSubject());
        paymentInfo.setCreateTime(alipayNotify.getGmtCreate());
        paymentInfo.setTotalAmount(alipayNotify.getTotalAmount());
        paymentInfo.setPaymentStatus(alipayNotify.getTradeStatus());
        paymentInfo.setCallbackTime(alipayNotify.getNotifyTime());
        paymentInfo.setConfirmTime(new Date());
        orderService.handlePayment(paymentInfo);

        return "success";
//        return "success";
    }
}
