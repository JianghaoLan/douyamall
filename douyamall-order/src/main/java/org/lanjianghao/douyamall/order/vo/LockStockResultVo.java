package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

@Data
public class LockStockResultVo {
    private Long skuId;
    private Long wareId;
    private Integer num;
}
