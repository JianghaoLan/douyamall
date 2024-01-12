package org.lanjianghao.douyamall.order.vo;

import lombok.Data;
import org.lanjianghao.douyamall.order.entity.OrderEntity;

@Data
public class SubmitOrderRespVo {
    private OrderEntity order;
    private Integer code;  //错误状态码

}
