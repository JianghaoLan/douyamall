package org.lanjianghao.douyamall.ware.service.impl;

import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.common.exception.ApplicationException;
import org.lanjianghao.douyamall.ware.entity.PurchaseDetailEntity;
import org.lanjianghao.douyamall.ware.exception.IncorrectPurchaseItemsException;
import org.lanjianghao.douyamall.ware.exception.IncorrectPurchaseStatusException;
import org.lanjianghao.douyamall.ware.service.PurchaseDetailService;
import org.lanjianghao.douyamall.ware.service.WareSkuService;
import org.lanjianghao.douyamall.ware.vo.AddStockVo;
import org.lanjianghao.douyamall.ware.vo.CompletePurchaseItem;
import org.lanjianghao.douyamall.ware.vo.CompletePurchaseVo;
import org.lanjianghao.douyamall.ware.vo.MergePurchaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.ware.dao.PurchaseDao;
import org.lanjianghao.douyamall.ware.entity.PurchaseEntity;
import org.lanjianghao.douyamall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService detailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceivedPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", WareConstant.PurchaseEnum.PURCHASE_STATUS_CREATED.getCode())
                        .or().eq("status", WareConstant.PurchaseEnum.PURCHASE_STATUS_ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergePurchaseVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchase.setStatus(WareConstant.PurchaseEnum.PURCHASE_STATUS_CREATED.getCode());
            this.save(purchase);
            purchaseId = purchase.getId();
        }

        //检查采购单是否已被领取
        PurchaseEntity purchase = this.getById(purchaseId);
        if (purchase.getStatus() != WareConstant.PurchaseEnum.PURCHASE_STATUS_CREATED.getCode()
                && purchase.getStatus() != WareConstant.PurchaseEnum.PURCHASE_STATUS_ASSIGNED.getCode()) {
            return;
        }

        List<Long> items = mergeVo.getItems();
        final Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> details = items.stream().map(id -> {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            detail.setId(id);
            detail.setPurchaseId(finalPurchaseId);
            detail.setStatus(WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_ASSIGNED.getCode());
            return detail;
        }).collect(Collectors.toList());
        detailService.updateBatchById(details);

        PurchaseEntity updatedPurchase = new PurchaseEntity();
        updatedPurchase.setId(purchaseId);
        updatedPurchase.setUpdateTime(new Date());
        this.updateById(updatedPurchase);
    }

    @Transactional
    @Override
    public void receive(List<Long> purchaseIds) {
        //更新采购单
        List<PurchaseEntity> purchases = this.listByIds(purchaseIds).stream().filter(purchase ->
                purchase.getStatus() == WareConstant.PurchaseEnum.PURCHASE_STATUS_CREATED.getCode()
                        || purchase.getStatus() == WareConstant.PurchaseEnum.PURCHASE_STATUS_ASSIGNED.getCode()
        ).peek(purchase -> {
            purchase.setStatus(WareConstant.PurchaseEnum.PURCHASE_STATUS_RECEIVED.getCode());
            purchase.setUpdateTime(new Date());
        }).collect(Collectors.toList());
        this.updateBatchById(purchases);

        //更新采购需求
        List<PurchaseDetailEntity> details =
                detailService.list(new QueryWrapper<PurchaseDetailEntity>().in("purchase_id", purchaseIds));
        List<PurchaseDetailEntity> updatedDetails = details.stream().map(detail -> {
            PurchaseDetailEntity updatedDetail = new PurchaseDetailEntity();
            updatedDetail.setId(detail.getId());
            updatedDetail.setStatus(WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_BUYING.getCode());
            return updatedDetail;
        }).collect(Collectors.toList());
        detailService.updateBatchById(updatedDetails);
    }

    private Integer getPurchaseStatus(Long purchaseId) {
        PurchaseEntity purchase = this.getById(purchaseId);
        if (purchase == null) {
            return null;
        }
        return purchase.getStatus();
    }

    private boolean hasDuplicateId(List<Long> ids) {
        return ids.stream().distinct().count() != ids.size();
    }

    private boolean validatePurchaseItems(CompletePurchaseVo vo) {
        if (vo == null || vo.getItems() == null) {
            return false;
        }

        List<Long> detailIds = vo.getItems().stream().map(CompletePurchaseItem::getItemId).collect(Collectors.toList());
        if (detailIds.isEmpty() || hasDuplicateId(detailIds)) {
            //id列表为空或有重复
            return false;
        }

        //判断采购需求是否与采购单对应，且采购需求状态为正在采购
        List<PurchaseDetailEntity> details = detailService.listByIds(detailIds);
        if (details.size() < detailIds.size()) {
            //有未找到的采购需求
            return false;
        }
        return details.stream().allMatch(detail ->
                detail.getStatus() == WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_BUYING.getCode()
                        && Objects.equals(detail.getPurchaseId(), vo.getId()));
    }

    @Transactional
    @Override
    public void completePurchase(CompletePurchaseVo vo)
            throws IncorrectPurchaseItemsException, IncorrectPurchaseStatusException {

        Long purchaseId = vo.getId();

        //判断采购单状态是否为已领取
        Integer purchaseStatus = getPurchaseStatus(purchaseId);
        if (purchaseStatus == null || purchaseStatus != WareConstant.PurchaseEnum.PURCHASE_STATUS_RECEIVED.getCode()) {
            throw new IncorrectPurchaseStatusException();
        }

        //判断采购需求是否有误
        if (!validatePurchaseItems(vo)) {
            throw new IncorrectPurchaseItemsException();
        }

        //更新采购需求的status
//        AtomicBoolean completed = new AtomicBoolean(true);
        List<PurchaseDetailEntity> details = vo.getItems().stream().map(item -> {
            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            detail.setId(item.getItemId());
            detail.setStatus(item.getStatus());
//            if (item.getStatus() == WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_FAILED.getCode()) {
//                completed.set(false);
//            }
            return detail;
        }).collect(Collectors.toList());
        detailService.updateBatchById(details);

        //更新采购单的status
        //只有在所有采购需求都完成（成功或失败），采购单才完成
        if (detailService.allDone(purchaseId)) {
            PurchaseEntity purchase = new PurchaseEntity();
            if (detailService.hasFailedItem(purchaseId)) {
                purchase.setStatus(WareConstant.PurchaseEnum.PURCHASE_STATUS_HASERROR.getCode());
            } else {
                purchase.setStatus(WareConstant.PurchaseEnum.PURCHASE_STATUS_COMPLETED.getCode());
            }
            purchase.setId(purchaseId);
            purchase.setUpdateTime(new Date());
            this.updateById(purchase);
        }

        //添加商品库存
        List<Long> completedDetailIds = details.stream()
                .filter(detail -> detail.getStatus() ==
                        WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_COMPLETED.getCode())
                .map(PurchaseDetailEntity::getId)
                .collect(Collectors.toList());
        if (!completedDetailIds.isEmpty()) {
            List<PurchaseDetailEntity> completedDetails = detailService.listByIds(completedDetailIds);
            List<AddStockVo> addStockVos = completedDetails.stream().map(detail -> {
                AddStockVo addStockVo = new AddStockVo();
                addStockVo.setSkuId(detail.getSkuId());
                addStockVo.setWareId(detail.getWareId());
                addStockVo.setStock(detail.getSkuNum());
                return addStockVo;
            }).collect(Collectors.toList());
            wareSkuService.addStocks(addStockVos);
        }
    }
}
