package org.lanjianghao.douyamall.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {
    private String orderSn;
    private List<LockItemVo> items;
}
