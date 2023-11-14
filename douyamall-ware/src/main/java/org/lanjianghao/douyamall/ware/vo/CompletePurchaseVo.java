package org.lanjianghao.douyamall.ware.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompletePurchaseVo {

    @NotNull
    private Long id;

    @NotEmpty
    @Valid
    private List<CompletePurchaseItem> items;
}
