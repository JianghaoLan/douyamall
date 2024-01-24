package org.lanjianghao.douyamall.order.web;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.douyamall.order.component.AlipayTemplate;
import org.lanjianghao.douyamall.order.exception.AlipayPayFailedException;
import org.lanjianghao.douyamall.order.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class PayWebController {

    @Autowired
    PayService payService;

    @ResponseBody
    @GetMapping(value = "/pay/alipay", produces = "text/html")
    public String alipay(@RequestParam("orderSn") String orderSn) {
        try {
            return payService.alipay(orderSn);
        } catch (AlipayPayFailedException e) {
            log.error(e.toString());
            return "支付失败";
        }
    }
}
