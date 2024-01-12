package org.lanjianghao.douyamall.cart.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.cart.exception.GetSkuPricesFailedException;
import org.lanjianghao.douyamall.cart.feign.ProductFeignService;
import org.lanjianghao.douyamall.cart.repository.CartRepository;
import org.lanjianghao.douyamall.cart.service.CartService;
import org.lanjianghao.douyamall.cart.to.UserInfoTo;
import org.lanjianghao.douyamall.cart.vo.CartItemVo;
import org.lanjianghao.douyamall.cart.vo.CartVo;
import org.lanjianghao.douyamall.cart.vo.SkuInfoVo;
import org.lanjianghao.douyamall.cart.vo.SkuPriceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ThreadPoolExecutor executor;

    private Map<Long, BigDecimal> getSkuPrices(List<Long> skuIds) {
        R r = productFeignService.prices(skuIds);
        if (r.getCode() == 0) {
            List<SkuPriceVo> skuPrices = r.get("data", new TypeReference<List<SkuPriceVo>>() {});
            return skuPrices.stream().collect(Collectors.toMap(SkuPriceVo::getSkuId, SkuPriceVo::getPrice));
        }
        throw new GetSkuPricesFailedException("获取商品价格失败");
    }

    private String getCurUserKey(UserInfoTo userInfo) {
        return userInfo.getUserId() == null ? userInfo.getUserKey() : userInfo.getUserId().toString();
    }

    @Override
    public CartItemVo addToCart(UserInfoTo userInfo, Long skuId, Integer num) {

        CartItemVo item = new CartItemVo();
        item.setChecked(true);
        item.setCount(num);

        CompletableFuture<Void> getSkuInfoFuture = CompletableFuture
                .supplyAsync(() -> productFeignService.getSkuInfo(skuId), executor)
                .thenAccept((r) -> {
                    SkuInfoVo skuInfo = r.get("skuInfo", SkuInfoVo.class);
                    item.setSkuId(skuInfo.getSkuId());
                    item.setPrice(skuInfo.getPrice());
                    item.setImage(skuInfo.getSkuDefaultImg());
                    item.setTitle(skuInfo.getSkuTitle());
                });

        CompletableFuture<Void> getAttrValueFuture = CompletableFuture
                .supplyAsync(() -> productFeignService.getSaleAttrValuesAsStringList(skuId), executor)
                .thenAccept((r) -> {
                    List<String> attrValues = r.get("data", new TypeReference<List<String>>() {
                    });
                    item.setSkuAttr(attrValues);
                });

        CompletableFuture<Void> addToCartFuture = CompletableFuture.allOf(getSkuInfoFuture, getAttrValueFuture)
                .thenRunAsync(() -> cartRepository.addToCart(getCurUserKey(userInfo), item), executor);

        try {
            addToCartFuture.join();
        } catch (CompletionException e) {
            throw new RuntimeException(e.getCause());
        }

        return item;
    }

    @Override
    public CartItemVo getUserCartItemBySkuId(UserInfoTo userInfo, Long skuId) {
        return cartRepository.getUserCartItemBySkuId(getCurUserKey(userInfo), skuId);
    }

    @Override
    public CartVo getCart(UserInfoTo userInfo) {
        if (userInfo.getUserId() != null) {
            migrateCartFromTempUserToLoginUser(userInfo);
        }

        String userKey = getCurUserKey(userInfo);
        List<CartItemVo> items = cartRepository.getCart(userKey);

        CartVo cart = new CartVo();
        cart.setItems(items);
        return cart;
    }

    @Override
    public void setItemChecked(UserInfoTo userInfo, Long skuId, boolean checked) {
        cartRepository.setItemChecked(getCurUserKey(userInfo), skuId, checked);
    }

    @Override
    public void setItemCount(UserInfoTo userInfo, Long skuId, Integer count) {
        cartRepository.setItemCount(getCurUserKey(userInfo), skuId, count);
    }

    private void migrateCartFromTempUserToLoginUser(UserInfoTo userInfo) {
        List<CartItemVo> items = cartRepository.getCart(userInfo.getUserKey());
        if (items != null) {
            items.forEach(item -> cartRepository.addToCart(userInfo.getUserId().toString(), item));
        }
        cartRepository.deleteCart(userInfo.getUserKey());
    }

    @Override
    public void deleteItem(UserInfoTo userInfo, Long skuId) {
        cartRepository.deleteItemBySkuId(getCurUserKey(userInfo), skuId);
    }

    @Override
    public List<CartItemVo> getCartCheckedItems(UserInfoTo userInfo) {
        String userKey = getCurUserKey(userInfo);
        List<CartItemVo> items = cartRepository.getCart(userKey);

        List<CartItemVo> checkedItems = items.stream().filter(CartItemVo::getChecked).collect(Collectors.toList());
        List<Long> skuIds = checkedItems.stream().map(CartItemVo::getSkuId).collect(Collectors.toList());
        Map<Long, BigDecimal> skuPrices = getSkuPrices(skuIds);
        checkedItems.forEach(item -> item.setPrice(skuPrices.get(item.getSkuId())));
        return checkedItems;
    }
}
