package org.lanjianghao.douyamall.coupon.vo;

import lombok.Data;
import org.lanjianghao.douyamall.coupon.entity.SeckillSessionEntity;
import org.lanjianghao.douyamall.coupon.entity.SeckillSkuRelationEntity;

import java.util.List;

@Data
public class SecKillSessionWithRelationsVo {
    SeckillSessionEntity session;
    List<SeckillSkuRelationEntity> relations;
}
