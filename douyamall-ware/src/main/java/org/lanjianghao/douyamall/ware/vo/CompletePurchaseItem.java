package org.lanjianghao.douyamall.ware.vo;

import lombok.Data;
import org.lanjianghao.douyamall.ware.validation.constraints.PurchaseDetailDoneStatus;

import javax.validation.constraints.NotNull;

@Data
public class CompletePurchaseItem {

    @NotNull
    private Long itemId;

    @PurchaseDetailDoneStatus
    private int status;

    private String reason;
}
