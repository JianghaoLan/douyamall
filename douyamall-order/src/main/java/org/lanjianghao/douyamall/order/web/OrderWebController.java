package org.lanjianghao.douyamall.order.web;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.order.entity.OrderEntity;
import org.lanjianghao.douyamall.order.exception.NoSelectedItemException;
import org.lanjianghao.douyamall.order.exception.OrderPriceCheckFailedException;
import org.lanjianghao.douyamall.order.exception.OrderTokenVerifyFailedException;
import org.lanjianghao.douyamall.order.exception.StockLockFailedException;
import org.lanjianghao.douyamall.order.interceptor.AuthInterceptor;
import org.lanjianghao.douyamall.order.service.OrderService;
import org.lanjianghao.douyamall.order.vo.OrderConfirmVo;
import org.lanjianghao.douyamall.order.vo.SubmitOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @RequestMapping("/totrade")
    public String toTrade(Model model) {
        MemberVo loginUser = AuthInterceptor.loginUser.get();
        OrderConfirmVo confirmVo = orderService.getConfirmOrder(loginUser);
        model.addAttribute("orderConfirmData", confirmVo);

        return "confirm";
    }

    @PostMapping("/submit")
    public String submitOrder(SubmitOrderVo submit, Model model, RedirectAttributes redirectAttributes) {
        MemberVo loginUser = AuthInterceptor.loginUser.get();
        try {
            OrderEntity order = orderService.submitOrder(loginUser, submit);
            model.addAttribute("order", order);

        } catch (NoSelectedItemException |
                 OrderPriceCheckFailedException |
                 StockLockFailedException |
                 OrderTokenVerifyFailedException e) {
            String errorMsg = "下单失败：" + e.getMessage();
            redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
            return "redirect:http://order.douyamall.com/totrade";
        }

        return "pay";
    }
}
