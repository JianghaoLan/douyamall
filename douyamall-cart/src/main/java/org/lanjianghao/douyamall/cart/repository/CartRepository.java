package org.lanjianghao.douyamall.cart.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.common.constant.CartConstant;
import org.lanjianghao.douyamall.cart.exception.SkuNotExistsException;
import org.lanjianghao.douyamall.cart.vo.CartItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class CartRepository {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private BoundHashOperations<String, Object, Object> getUserCartOps(String userKey) {
        String userRedisKey = CartConstant.CART_REDIS_KEY_PREFIX + userKey;
        return redisTemplate.boundHashOps(userRedisKey);
    }

    private CartItemVo getCartItemBySkuId(BoundHashOperations<String, Object, Object> userCartOps, String skuId) {
        String cartItemJson = (String)userCartOps.get(skuId);
        if (cartItemJson == null) {
            return null;
        }

        try {
            return objectMapper.readValue(cartItemJson, CartItemVo.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public CartItemVo getUserCartItemBySkuId(String userKey, Long skuId) {
        BoundHashOperations<String, Object, Object> userCartOps = getUserCartOps(userKey);
        return getCartItemBySkuId(userCartOps, skuId.toString());
    }

    public void addToCart(String userKey, CartItemVo item) {
        BoundHashOperations<String, Object, Object> userCartOps = getUserCartOps(userKey);

        //如果已经有该商品，仅修改数量
        CartItemVo savedItem = getCartItemBySkuId(userCartOps, item.getSkuId().toString());
        if (savedItem != null) {
            item.setCount(item.getCount() + savedItem.getCount());
        }

        try {
            userCartOps.put(item.getSkuId().toString(), objectMapper.writeValueAsString(item));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CartItemVo> getCart(String userKey) {
        BoundHashOperations<String, Object, Object> userCartOps = getUserCartOps(userKey);
        List<Object> itemJsons = userCartOps.values();
        if (itemJsons == null) {
            return null;
        }

        return itemJsons.stream().map(itemJson -> {
            try {
                return objectMapper.readValue((String) itemJson, CartItemVo.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void deleteCart(String userKey) {
        String redisKey = CartConstant.CART_REDIS_KEY_PREFIX + userKey;
        redisTemplate.delete(redisKey);
    }

    public void setItemChecked(String userKey, Long skuId, boolean checked) {
        BoundHashOperations<String, Object, Object> userCartOps = getUserCartOps(userKey);
        CartItemVo item = getCartItemBySkuId(userCartOps, skuId.toString());
        if (item == null) {
            throw new SkuNotExistsException();
        }
        item.setChecked(checked);
        try {
            userCartOps.put(skuId.toString(), objectMapper.writeValueAsString(item));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setItemCount(String userKey, Long skuId, Integer count) {
        BoundHashOperations<String, Object, Object> userCartOps = getUserCartOps(userKey);
        CartItemVo item = getCartItemBySkuId(userCartOps, skuId.toString());
        if (item == null) {
            throw new SkuNotExistsException();
        }
        item.setCount(count);
        try {
            userCartOps.put(skuId.toString(), objectMapper.writeValueAsString(item));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteItemBySkuId(String userKey, Long skuId) {
        BoundHashOperations<String, Object, Object> userCartOps = getUserCartOps(userKey);
        userCartOps.delete(skuId.toString());
    }
}
