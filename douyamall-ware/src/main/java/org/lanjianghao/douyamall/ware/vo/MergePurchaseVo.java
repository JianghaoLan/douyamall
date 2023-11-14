package org.lanjianghao.douyamall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergePurchaseVo {
    private Long purchaseId;
    private List<Long> items;
}
