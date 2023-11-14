package org.lanjianghao.douyamall.ware.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddStockVo {
    @NotNull
    private Long skuId;

    @NotNull
    private Long wareId;

    @NotNull
    @Min(0)
    private Integer stock;
}
