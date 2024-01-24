package org.lanjianghao.douyamall.seckill.vo;

import lombok.Data;

import java.util.List;

@Data
public class SecKillSessionWithRelationsVo {
    SeckillSessionVo session;
    List<SeckillSkuRelationVo> relations;
}
