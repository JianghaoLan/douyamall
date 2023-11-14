package org.lanjianghao.douyamall.ware.service.impl;

import org.lanjianghao.common.constant.WareConstant;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.ware.dao.PurchaseDetailDao;
import org.lanjianghao.douyamall.ware.entity.PurchaseDetailEntity;
import org.lanjianghao.douyamall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("id", key).or().eq("purchase_id", key).or().eq("sku_id", key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.hasLength(status)) {
            queryWrapper.eq("status", status);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.hasLength(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public boolean allDone(Long purchaseId) {
        long countItems = this.count(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId));
        long countDoneItems = this.count(new QueryWrapper<PurchaseDetailEntity>()
                .eq("purchase_id", purchaseId)
                .and(wrapper -> wrapper.in("status", Arrays.asList(
                        WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_FAILED.getCode(),
                        WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_COMPLETED.getCode())))
        );
        return countItems == countDoneItems;
    }

    @Override
    public boolean hasFailedItem(Long purchaseId) {
        return this.count(new QueryWrapper<PurchaseDetailEntity>()
                .eq("purchase_id", purchaseId)
                .eq("status", WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_FAILED.getCode())
        ) > 0;
    }

}