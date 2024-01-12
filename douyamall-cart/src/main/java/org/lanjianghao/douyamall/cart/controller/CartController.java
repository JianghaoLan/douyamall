package org.lanjianghao.douyamall.cart.controller;

import org.lanjianghao.douyamall.cart.interceptor.CartInterceptor;
import org.lanjianghao.douyamall.cart.service.CartService;
import org.lanjianghao.douyamall.cart.to.UserInfoTo;
import org.lanjianghao.douyamall.cart.vo.CartItemVo;
import org.lanjianghao.douyamall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage(Model model) {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        CartVo cart = cartService.getCart(userInfo);
        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/add")
    public String add(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes ra) {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();

        cartService.addToCart(userInfo, skuId, num);
        ra.addAttribute("skuId", skuId);

        return "redirect:http://cart.douyamall.com/success.html";
    }

    @GetMapping("/success.html")
    public String successPage(@RequestParam("skuId") Long skuId, Model model) {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();

        CartItemVo cartItem = cartService.getUserCartItemBySkuId(userInfo, skuId);
        if (cartItem != null) {
            model.addAttribute("item", cartItem);
        }

        return "success";
    }

    @GetMapping("/checkItem")
    public String setItemChecked(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer checked) {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();

        boolean boolChecked = checked == 1;
        cartService.setItemChecked(userInfo, skuId, boolChecked);
        return "redirect:http://cart.douyamall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String setItemCount(@RequestParam("skuId") Long skuId, @RequestParam("count") Integer count) {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        cartService.setItemCount(userInfo, skuId, count);
        return "redirect:http://cart.douyamall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        cartService.deleteItem(userInfo, skuId);
        return "redirect:http://cart.douyamall.com/cart.html";
    }

    @GetMapping("/list/checked")
    @ResponseBody
    public List<CartItemVo> listUserCartCheckedItems() {
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        if (userInfo.getUserId() == null) {
            return null;
        }
        return cartService.getCartCheckedItems(userInfo);
    }
}
