package org.lanjianghao.douyamall.seckill.service;

import org.lanjianghao.common.to.CreateSecKillOrderTo;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.seckill.exception.*;
import org.lanjianghao.douyamall.seckill.vo.SeckillSkuRedisTo;

import java.util.List;

public interface SecKillService {
    void upUpcomingSku(Long days);

    List<SeckillSkuRedisTo> getCurrentSecKillSkus();

    SeckillSkuRedisTo getSecKillSkuBySkuId(Long skuId);

    String kill(MemberVo memberVo, Long skuId, Long sessionId, Integer num, String token) throws
            SecKillSkuNotExistsException,
            SecKillTokenVerifyFailedException,
            NotSkuKillTimeException,
            KillNumExceedsUserLimitException, InsufficientSkuStockException;

    void releaseStockForSecKillOrder(CreateSecKillOrderTo createSecKillOrderTo);
}
