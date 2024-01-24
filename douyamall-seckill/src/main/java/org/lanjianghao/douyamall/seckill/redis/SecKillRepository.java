package org.lanjianghao.douyamall.seckill.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.douyamall.seckill.vo.SecKillSessionWithRelationsVo;
import org.lanjianghao.douyamall.seckill.vo.SeckillSkuRedisTo;
import org.lanjianghao.douyamall.seckill.vo.SeckillSkuRelationVo;
import org.redisson.api.RLock;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class SecKillRepository {

    private static final String SESSION_REDIS_KEY_PREFIX = "seckill:session:";
    private static final String SKU_REDIS_KEY_PREFIX = "seckill:sku:";
    private static final String SKU_STOCK_SEMAPHORE_PREFIX = "seckill:stock:";
    private static final String UPLOAD_LOCK_KEY = "seckill:upload:lock";
    private static final String USER_STOCK_REDIS_KEY_PREFIX = "seckill:kill:";
    private static final String ORDER_REDIS_KEY_PREFIX = "seckill:order:";

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    public void saveSessionInfos(List<SecKillSessionWithRelationsVo> sessions) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        sessions.forEach(session -> {
            long start = session.getSession().getStartTime().getTime();
            long end = session.getSession().getEndTime().getTime();
            String redisKey = SESSION_REDIS_KEY_PREFIX + start + "_" + end;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                return;
            }

            List<SeckillSkuRelationVo> relations = session.getRelations();
            if (CollectionUtils.isEmpty(relations)) {
                return;
            }
            List<String> values = relations.stream().map(SecKillRepository::getSkuRedisKey).collect(Collectors.toList());
//            redisTemplate.delete(redisKey);
            listOps.rightPushAll(redisKey, values);
            redisTemplate.expireAt(redisKey, session.getSession().getEndTime());
        });
    }

    @NotNull
    private static String getSkuRedisKey(SeckillSkuRelationVo r) {
        //sessionId_skuId
        return r.getPromotionSessionId().toString() + "_" + r.getSkuId();
    }

    @NotNull
    private static String getSkuRedisKey(SeckillSkuRedisTo to) {
        //sessionId_skuId
        return SKU_REDIS_KEY_PREFIX + to.getPromotionSessionId().toString() + "_" + to.getSkuId();
    }

    @NotNull
    private static String getSkuRedisKey(Long sessionId, Long skuId) {
        return SKU_REDIS_KEY_PREFIX + sessionId.toString() + "_" + skuId;
    }

    private static String getSkuRedisKeyBySessionItemValue(String value) {
        return SKU_REDIS_KEY_PREFIX + value;
    }

    private static SkuRedisKeyInfo parseSkuRedisKey(String redisKey) {
        String[] s = redisKey.replace(SKU_REDIS_KEY_PREFIX, "").split("_", 2);
        SkuRedisKeyInfo keyInfo = new SkuRedisKeyInfo();
        keyInfo.setSessionId(Long.parseLong(s[0]));
        keyInfo.setSkuId(Long.parseLong(s[1]));
        return keyInfo;
    }

    private static SessionRedisKeyInfo parseSessionRedisKey(String sessionKey) {
        String[] s = sessionKey.replace(SESSION_REDIS_KEY_PREFIX, "").split("_", 2);
        return new SessionRedisKeyInfo(Long.parseLong(s[0]), Long.parseLong(s[1]));
    }

    public void saveSessionSkus(List<SeckillSkuRedisTo> skus) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        skus.forEach(sku -> {
            //设置库存信号量（redis中信号量到时间会自动过期）
            RPermitExpirableSemaphore sem = getStockPermitExpirableSemaphore(sku);
            sem.trySetPermits(sku.getSeckillCount());
            sem.expire(new Date(sku.getEndTime()).toInstant());

            //保存并设置过期时间
            String key = getSkuRedisKey(sku);
            String value;
            try {
                value = objectMapper.writeValueAsString(sku);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ops.set(key, value);
            redisTemplate.expireAt(key, new Date(sku.getEndTime()));        //设置商品信息过期时间
        });
    }

    private RSemaphore getStockSemaphore(Long sessionId, Long skuId) {
        String semKey = SKU_STOCK_SEMAPHORE_PREFIX + sessionId + "_" + skuId;
        return redissonClient.getSemaphore(semKey);
    }

    private RSemaphore getStockSemaphore(SeckillSkuRedisTo sku) {
        return getStockSemaphore(sku.getPromotionSessionId(), sku.getSkuId());
    }

    private RPermitExpirableSemaphore getStockPermitExpirableSemaphore(SeckillSkuRedisTo sku) {
        String semKey = SKU_STOCK_SEMAPHORE_PREFIX + sku.getPromotionSessionId() + "_" + sku.getSkuId();
        return redissonClient.getPermitExpirableSemaphore(semKey);
    }

    public boolean tryAcquireStock(SeckillSkuRedisTo sku, Integer num, String orderSn) {
        RSemaphore sem = getStockSemaphore(sku);
        setOrder(orderSn, sku, num, sku.getEndTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
        return sem.tryAcquire(num);
    }

    public void releaseStock(CreateSecKillOrderTo createSecKillOrderTo) {
        if (deleteOrder(createSecKillOrderTo.getOrderSn())) {
            RSemaphore sem = getStockSemaphore(createSecKillOrderTo.getPromotionSessionId(),
                    createSecKillOrderTo.getSkuId());
            sem.release(createSecKillOrderTo.getNum());
        }
    }

    private void setOrder(String orderSn, SeckillSkuRedisTo sku, Integer num, long timeout, TimeUnit unit) {
        OrderRedisTo orderRedisTo = new OrderRedisTo(sku.getPromotionSessionId(), sku.getSkuId(), num);
        try {
            String orderJson = objectMapper.writeValueAsString(orderRedisTo);
            redisTemplate.opsForValue().set(ORDER_REDIS_KEY_PREFIX + orderSn, orderJson, timeout, unit);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean deleteOrder(String orderSn) {
        return Boolean.TRUE.equals(redisTemplate.delete(ORDER_REDIS_KEY_PREFIX + orderSn));
    }

    public RLock getUploadLock() {
        return redissonClient.getLock(UPLOAD_LOCK_KEY);
    }

    public List<SeckillSkuRedisTo> getSessionSkusByTime(Date date, boolean removeToken) {
        return getSessionSkusByTime(date.getTime(), removeToken);
    }

    public List<SeckillSkuRedisTo> getSessionSkusByTime(long time, boolean removeToken) {
//        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUS_REDIS_KEY);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        Set<String> keys = redisTemplate.keys(SESSION_REDIS_KEY_PREFIX + "*");
        if (keys == null) {
            return null;
        }
        for (String key : keys) {
            SessionRedisKeyInfo keyInfo = parseSessionRedisKey(key);
            if (time >= keyInfo.getStartTime() && time <= keyInfo.getEndTime()) {
                List<String> values = redisTemplate.opsForList().range(key, 0, -1);
                assert values != null;
                List<String> skuKeys = values.stream().map(SecKillRepository::getSkuRedisKeyBySessionItemValue).collect(Collectors.toList());
                List<String> skuJsons = ops.multiGet(skuKeys);
                assert skuJsons != null;
                return skuJsons.stream().map(json -> {
                    try {
                        if (json == null) {
                            throw new RuntimeException("无法在Redis中找到SKU");
                        }
                        SeckillSkuRedisTo sku = objectMapper.readValue(json, SeckillSkuRedisTo.class);
                        if (removeToken) {
                            sku.setRandomToken(null);
                        }
                        return sku;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
            }
        }
        return null;
    }

    public SeckillSkuRedisTo getSkuBySkuId(Long skuId, Date from) {
//        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUS_REDIS_KEY);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys(SKU_REDIS_KEY_PREFIX + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                SkuRedisKeyInfo keyInfo = parseSkuRedisKey(key);
                if (keyInfo.getSkuId().equals(skuId)) {
                    SeckillSkuRedisTo sku = this.getSku(ops, key);
                    if (sku != null && sku.getEndTime() >= from.getTime()) {
                        return sku;
                    }
                }
            }
        }
        return null;
    }

    public void removeTokenIfNotBetween(SeckillSkuRedisTo sku, Date date) {
        long time = date.getTime();
        if (!(sku.getStartTime() <= time && sku.getEndTime() >= time)) {
            sku.setRandomToken(null);
        }
    }

    private SeckillSkuRedisTo getSku(ValueOperations<String, String> valOps, String key) {
        String skuJson = valOps.get(key);
        if (skuJson == null) {
            return null;
        }
        try {
            return objectMapper.readValue(skuJson, SeckillSkuRedisTo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public SeckillSkuRedisTo getSkuBySessionIdAndSkuId(Long sessionId, Long skuId, boolean removeToken) {
        SeckillSkuRedisTo sku = getSku(redisTemplate.opsForValue(), getSkuRedisKey(sessionId, skuId));
        if (sku != null && removeToken) {
            sku.setRandomToken(null);
        }
        return sku;
    }

    public Boolean tryAcquireUserStock(Long userId, SeckillSkuRedisTo sku, Integer num) {
        RSemaphore sem = redissonClient.getSemaphore(getUserStockRedisKey(userId, sku));
        sem.trySetPermits(sku.getSeckillLimit());
        redisTemplate.expireAt(getUserStockRedisKey(userId, sku), new Date(sku.getEndTime()));
        return sem.tryAcquire(num);
    }

    public void releaseUserStock(Long userId, SeckillSkuRedisTo sku, Integer num) {
        RSemaphore sem = redissonClient.getSemaphore(getUserStockRedisKey(userId, sku));
        sem.release(num);
    }

    public void releaseUserStock(CreateSecKillOrderTo to) {
        RSemaphore sem = redissonClient.getSemaphore(getUserStockRedisKey(to.getMemberId(), to.getPromotionSessionId(), to.getSkuId()));
        sem.release(to.getNum());
    }

    private String getUserStockRedisKey(Long userId, Long sessionId, Long skuId) {
        return USER_STOCK_REDIS_KEY_PREFIX + userId + "_" + sessionId + "_" + skuId;
    }

    private String getUserStockRedisKey(Long userId, SeckillSkuRedisTo sku) {
        return getUserStockRedisKey(userId, sku.getPromotionSessionId(), sku.getSkuId());
    }

    @Data
    private static class SkuRedisKeyInfo {
        private Long sessionId;
        private Long skuId;

        public String getSkuRedisKey() {
            return SecKillRepository.getSkuRedisKey(sessionId, skuId);
        }
    }

    @Data
    @AllArgsConstructor
    private static class SessionRedisKeyInfo {
        private Long startTime;
        private Long endTime;
    }

    @Data
    @AllArgsConstructor
    private static class OrderRedisTo {
        private Long sessionId;
        private Long skuId;
        private Integer num;
    }
}
