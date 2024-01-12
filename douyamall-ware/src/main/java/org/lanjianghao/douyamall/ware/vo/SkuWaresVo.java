package org.lanjianghao.douyamall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuWaresVo {
    private Long skuId;
    private Integer num;
    private List<Long> wareIds;
}
