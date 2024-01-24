package org.lanjianghao.douyamall.member.vo;

import lombok.Data;

import java.util.List;

@Data
public class MemberOrderVo {
    private OrderVo order;
    private List<OrderItemVo> items;
}
