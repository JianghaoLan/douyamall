package org.lanjianghao.douyamall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.douyamall.seckill.redis.SecKillRepository;
import org.lanjianghao.douyamall.seckill.service.SecKillService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架
 */
@Slf4j
@Component
public class SecKillSchedule {

    @Autowired
    SecKillService secKillService;

    @Autowired
    SecKillRepository secKillRepository;

    //    @Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void upSecKillSku() {
        RLock lock = secKillRepository.getUploadLock();
        lock.lock(10, TimeUnit.SECONDS);
        try {
            log.info("准备上架近期秒杀商品");
            secKillService.upUpcomingSku(3L);
            log.info("上架秒杀商品完毕");
        } finally {
            lock.unlock();
        }

    }
}
