package org.lanjianghao.douyamall.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jetbrains.annotations.NotNull;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.seckill.exception.*;
import org.lanjianghao.douyamall.seckill.feign.CouponFeignService;
import org.lanjianghao.douyamall.seckill.feign.ProductFeignService;
import org.lanjianghao.douyamall.seckill.redis.SecKillRepository;
import org.lanjianghao.douyamall.seckill.service.SecKillService;
import org.lanjianghao.douyamall.seckill.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    SecKillRepository secKillRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void upUpcomingSku(Long days) {
        R getUpcomingSessionsR = couponFeignService.getUpcomingSessions(days);
        if (getUpcomingSessionsR.getCode() == 0) {
            List<SecKillSessionWithRelationsVo> sessions = getUpcomingSessionsR.get("data",
                    new TypeReference<List<SecKillSessionWithRelationsVo>>() {
                    });
            if (CollectionUtils.isEmpty(sessions)) {
                return;
            }

            secKillRepository.saveSessionInfos(sessions);

            List<SeckillSkuRedisTo> redisTos = getSkuRedisTos(sessions);
            if (CollectionUtils.isEmpty(redisTos)) {
                return;
            }
            secKillRepository.saveSessionSkus(redisTos);
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSecKillSkus() {
        return secKillRepository.getSessionSkusByTime(new Date(), false);
    }

    @Override
    public SeckillSkuRedisTo getSecKillSkuBySkuId(Long skuId) {
        SeckillSkuRedisTo sku = secKillRepository.getSkuBySkuId(skuId, new Date());
        if (sku == null) {
            return null;
        }
        secKillRepository.removeTokenIfNotBetween(sku, new Date());
        return sku;
    }

    @Override
    public String kill(MemberVo memberVo, Long skuId, Long sessionId, Integer num, String token)
            throws SecKillSkuNotExistsException,
            SecKillTokenVerifyFailedException,
            NotSkuKillTimeException,
            KillNumExceedsUserLimitException,
            InsufficientSkuStockException {
        SeckillSkuRedisTo sku = secKillRepository.getSkuBySessionIdAndSkuId(sessionId, skuId, false);
        if (sku == null) {
            throw new SecKillSkuNotExistsException("秒杀商品不存在");
        }
        if (!checkTimeBetween(new Date(), sku.getStartTime(), sku.getEndTime())) {
            throw new NotSkuKillTimeException("当前不在商品秒杀时间");
        }
        if (!sku.getRandomToken().equals(token)) {
            throw new SecKillTokenVerifyFailedException("商品Token校验失败");
        }
        Boolean success = secKillRepository.tryAcquireUserStock(memberVo.getId(), sku, num);
        if (!success) {
            throw new KillNumExceedsUserLimitException("您已超出该商品的秒杀数量上限");
        }

        String orderSn = IdWorker.getTimeId();
        boolean stockAcquired = secKillRepository.tryAcquireStock(sku, num, orderSn);
        if (!stockAcquired) {
            secKillRepository.releaseUserStock(memberVo.getId(), sku, num);
            throw new InsufficientSkuStockException("商品数量不足");
        }
        CreateSecKillOrderTo createSecKillOrderTo = buildCreateSecKillOrderTo(sku, memberVo, orderSn, num);
        sendCreateOrderToMq(createSecKillOrderTo);

        //TODO 给订单系统发送创建订单消息
        return orderSn;
    }

    @Override
    public void releaseStockForSecKillOrder(CreateSecKillOrderTo createSecKillOrderTo) {
        secKillRepository.releaseStock(createSecKillOrderTo);
        secKillRepository.releaseUserStock(createSecKillOrderTo);
    }

    private void sendCreateOrderToMq(CreateSecKillOrderTo createSecKillOrderTo) {
        rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", createSecKillOrderTo);
    }

    @NotNull
    private CreateSecKillOrderTo buildCreateSecKillOrderTo(SeckillSkuRedisTo sku, MemberVo memberVo, String orderSn, Integer num) {
        CreateSecKillOrderTo createSecKillOrderTo = new CreateSecKillOrderTo();
        createSecKillOrderTo.setOrderSn(orderSn);
        createSecKillOrderTo.setNum(num);
        createSecKillOrderTo.setSkuId(sku.getSkuId());
        createSecKillOrderTo.setMemberId(memberVo.getId());
        createSecKillOrderTo.setSeckillPrice(sku.getSeckillPrice());
        createSecKillOrderTo.setPromotionSessionId(sku.getPromotionSessionId());
        return createSecKillOrderTo;
    }

    private boolean checkTimeBetween(Date date, Long startTime, Long endTime) {
        long time = date.getTime();
        return time >= startTime && time <= endTime;
    }

    private List<SeckillSkuRedisTo> getSkuRedisTos(List<SecKillSessionWithRelationsVo> sessions) {
        List<SeckillSkuRedisTo> relations = sessions.stream()
                .map(session -> {
                    SeckillSessionVo sessionInfo = session.getSession();
                    if (session.getRelations() == null) {
                        return Collections.<SeckillSkuRedisTo>emptyList();
                    }
                    return session.getRelations().stream()
                            .map(relation -> {
                                SeckillSkuRedisTo skuRedisTo = new SeckillSkuRedisTo();
                                BeanUtils.copyProperties(relation, skuRedisTo);
                                skuRedisTo.setStartTime(sessionInfo.getStartTime().getTime());
                                skuRedisTo.setEndTime(sessionInfo.getEndTime().getTime());
                                return skuRedisTo;
                            }).collect(Collectors.toList());
                })
                .reduce(new ArrayList<>(),
                        (rs, other) -> {
                            rs.addAll(other);
                            return rs;
                        });
        if (CollectionUtils.isEmpty(relations)) {
            return relations;
        }
        List<Long> skuIds = relations.stream().map(
                SeckillSkuRedisTo::getSkuId).distinct().collect(Collectors.toList());
        R skuInfosBySkuIdsR = productFeignService.getSkuInfosBySkuIds(skuIds);
        if (skuInfosBySkuIdsR.getCode() != 0) {
            throw new RuntimeException("查询SKU信息失败");
        }
        List<SkuInfoVo> skuInfos = skuInfosBySkuIdsR.get("data", new TypeReference<List<SkuInfoVo>>() {
        });
        Map<Long, SkuInfoVo> skuId2InfoMap = skuInfos.stream().collect(
                Collectors.toMap(SkuInfoVo::getSkuId, info -> info));

        return relations.stream().peek(relation -> {
            relation.setInfo(skuId2InfoMap.get(relation.getSkuId()));
            relation.setRandomToken(generateRandomToken());
        }).collect(Collectors.toList());
    }

    private String generateRandomToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
