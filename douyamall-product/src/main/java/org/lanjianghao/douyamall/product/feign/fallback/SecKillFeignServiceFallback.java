package org.lanjianghao.douyamall.product.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.exception.BizCodeEnum;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.product.feign.SecKillFeignService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecKillFeignServiceFallback implements SecKillFeignService {
    @Override
    public R getSecKillInfo(Long skuId) {
        log.error("查询秒杀信息失败，执行熔断");
        return R.error(BizCodeEnum.SEC_KILL_SERVICE_ACCESS_FAILED_EXCEPTION.getCode(),
                BizCodeEnum.SEC_KILL_SERVICE_ACCESS_FAILED_EXCEPTION.getMessage());
    }
}
