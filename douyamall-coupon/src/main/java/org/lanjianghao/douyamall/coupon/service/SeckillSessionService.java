package org.lanjianghao.douyamall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.coupon.entity.SeckillSessionEntity;
import org.lanjianghao.douyamall.coupon.vo.SecKillSessionWithRelationsVo;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:11:38
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SecKillSessionWithRelationsVo> listUpcomingSessionWithRelations(Long days);
}

