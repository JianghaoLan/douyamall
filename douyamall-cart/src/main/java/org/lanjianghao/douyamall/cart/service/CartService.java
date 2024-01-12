package org.lanjianghao.douyamall.cart.service;

import org.lanjianghao.douyamall.cart.to.UserInfoTo;
import org.lanjianghao.douyamall.cart.vo.CartItemVo;
import org.lanjianghao.douyamall.cart.vo.CartVo;

import java.util.List;

public interface CartService {
    CartItemVo addToCart(UserInfoTo userInfo, Long skuId, Integer num);

    CartItemVo getUserCartItemBySkuId(UserInfoTo userInfo, Long skuId);

    CartVo getCart(UserInfoTo userInfo);

    void setItemChecked(UserInfoTo userInfo, Long skuId, boolean boolChecked);

    void setItemCount(UserInfoTo userInfo, Long skuId, Integer count);

    void deleteItem(UserInfoTo userInfo, Long skuId);

    List<CartItemVo> getCartCheckedItems(UserInfoTo userInfo);
}
