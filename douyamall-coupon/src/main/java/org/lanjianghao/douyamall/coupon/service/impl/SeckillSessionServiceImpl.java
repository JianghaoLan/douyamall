package org.lanjianghao.douyamall.coupon.service.impl;

import org.lanjianghao.douyamall.coupon.entity.SeckillSkuRelationEntity;
import org.lanjianghao.douyamall.coupon.service.SeckillSkuRelationService;
import org.lanjianghao.douyamall.coupon.vo.SecKillSessionWithRelationsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.coupon.dao.SeckillSessionDao;
import org.lanjianghao.douyamall.coupon.entity.SeckillSessionEntity;
import org.lanjianghao.douyamall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SecKillSessionWithRelationsVo> listUpcomingSessionWithRelations(Long days) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String start = LocalDateTime.of(today, LocalTime.MIN).format(formatter);
        String end = LocalDateTime.of(today.plusDays(days - 1), LocalTime.MAX).format(formatter);

        List<SeckillSessionEntity> sessions = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", start, end));
        if (CollectionUtils.isEmpty(sessions)) {
            return Collections.emptyList();
        }
        List<Long> sessionIds = sessions.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());

        List<SeckillSkuRelationEntity> relations = seckillSkuRelationService.listBySessionIds(sessionIds);
        Map<Long, List<SeckillSkuRelationEntity>> sessionId2RelationMap = relations.stream().reduce(
                new HashMap<>(),
                (result, relation) -> {
                    List<SeckillSkuRelationEntity> rs = result.getOrDefault(relation.getPromotionSessionId(), new ArrayList<>());
                    rs.add(relation);
                    result.put(relation.getPromotionSessionId(), rs);
                    return result;
                },
                (map, other) -> {
                    map.putAll(other);
                    return map;
                });

        return sessions.stream().map(session -> {
            SecKillSessionWithRelationsVo sessionWithRelations = new SecKillSessionWithRelationsVo();
            sessionWithRelations.setSession(session);
            sessionWithRelations.setRelations(sessionId2RelationMap.get(session.getId()));
            return sessionWithRelations;
        }).collect(Collectors.toList());
    }

}