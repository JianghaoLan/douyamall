package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemVo {
    private Long skuId;
    private Boolean checked = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private Boolean hasStock;

    /**
     * 计算总价
     *
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }
}
