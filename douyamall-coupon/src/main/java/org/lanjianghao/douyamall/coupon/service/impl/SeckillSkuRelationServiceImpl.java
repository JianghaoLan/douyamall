package org.lanjianghao.douyamall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.coupon.dao.SeckillSkuRelationDao;
import org.lanjianghao.douyamall.coupon.entity.SeckillSkuRelationEntity;
import org.lanjianghao.douyamall.coupon.service.SeckillSkuRelationService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<>();

        String promotionSessionId = (String)params.get("promotionSessionId");
        if (StringUtils.hasLength(promotionSessionId)) {
            queryWrapper = queryWrapper.eq("promotion_session_id", params.get("promotionSessionId"));
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSkuRelationEntity> listBySessionIds(List<Long> sessionIds) {
        if (CollectionUtils.isEmpty(sessionIds)) {
            return null;
        }
        return this.list(new QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", sessionIds));
    }

}