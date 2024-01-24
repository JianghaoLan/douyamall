package org.lanjianghao.douyamall.seckill.controller;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.seckill.exception.*;
import org.lanjianghao.douyamall.seckill.interceptor.AuthInterceptor;
import org.lanjianghao.douyamall.seckill.service.SecKillService;
import org.lanjianghao.douyamall.seckill.vo.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class SecKillController {

    @Autowired
    SecKillService secKillService;

    @GetMapping("/skus/current")
    @ResponseBody
    public R getCurrentSecKillSkus() {
        List<SeckillSkuRedisTo> result = secKillService.getCurrentSecKillSkus();
        return R.ok().put("data", result);
    }

    @GetMapping("/sku/{skuId}")
    @ResponseBody
    public R getSecKillSku(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo sku = secKillService.getSecKillSkuBySkuId(skuId);
        return R.ok().put("data", sku);
    }

    @GetMapping("/kill")
    public String kill(@RequestParam("skuId") @NotNull Long skuId,
                       @RequestParam("sessionId") @NotNull Long sessionId,
                       @RequestParam("token") @NotBlank String token,
                       @RequestParam("num") @NotNull Integer num,
                       Model model) {
        MemberVo memberVo = AuthInterceptor.loginUser.get();
        try {
            String orderSn = secKillService.kill(memberVo, skuId, sessionId, num, token);
            model.addAttribute("orderSn", orderSn);
        } catch (SecKillFailedException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "success";
    }
}
