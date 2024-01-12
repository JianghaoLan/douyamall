package org.lanjianghao.douyamall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {
    private List<CartItemVo> items;
    private BigDecimal reduce = new BigDecimal(0);

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    public Integer getNumItems() {
        if (items != null) {
            int count = 0;
            for (CartItemVo item : items) {
                count += item.getCount();
            }
            return count;
        }
        return 0;
    }

    public Integer getNumItemTypes() {
        return items == null ? 0 : items.size();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = new BigDecimal(0);
        if (items != null) {
            for (CartItemVo item : items) {
                if (!item.getChecked()) {
                    continue;
                }
                totalPrice = totalPrice.add(item.getTotalPrice());
            }
        }
        totalPrice = totalPrice.subtract(getReduce());
        return totalPrice;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
