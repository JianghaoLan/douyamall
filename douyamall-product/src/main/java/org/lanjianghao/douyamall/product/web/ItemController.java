package org.lanjianghao.douyamall.product.web;

import org.lanjianghao.douyamall.product.service.SkuInfoService;
import org.lanjianghao.douyamall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {

        SkuItemVo item = skuInfoService.getItem(skuId);
        model.addAttribute("item", item);

        return "item";
    }
}
