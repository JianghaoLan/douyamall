package org.lanjianghao.douyamall.ware.service.impl;

import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.common.to.OrderTo;
import org.lanjianghao.common.to.WareOrderTaskTo;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.ware.entity.WareOrderTaskDetailEntity;
import org.lanjianghao.douyamall.ware.entity.WareOrderTaskEntity;
import org.lanjianghao.douyamall.ware.enume.OrderStatusEnum;
import org.lanjianghao.douyamall.ware.exception.NoEnoughStockException;
import org.lanjianghao.douyamall.ware.feign.OrderFeignService;
import org.lanjianghao.douyamall.ware.feign.ProductFeignService;
import org.lanjianghao.douyamall.ware.service.WareOrderTaskDetailService;
import org.lanjianghao.douyamall.ware.service.WareOrderTaskService;
import org.lanjianghao.douyamall.ware.vo.*;
import org.lanjianghao.common.to.SkuHasStockTo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.ware.dao.WareSkuDao;
import org.lanjianghao.douyamall.ware.entity.WareSkuEntity;
import org.lanjianghao.douyamall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String wareId = (String)params.get("wareId");
        if (StringUtils.hasLength(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        String skuId = (String)params.get("skuId");
        if (StringUtils.hasLength(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    private String getSkuName(Long skuId) {
        if (skuId == null) {
            return null;
        }
        try {
            R r = productFeignService.getSkuInfo(skuId);
            if (r.getCode() == 0) {
                Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
                return (String) skuInfo.get("skuName");
            }
        } catch (Exception ignored) {}

        return null;
    }

    @Override
    public void addStocks(List<AddStockVo> addStockVos) {
        addStockVos.forEach(vo -> {
            boolean exists = this.baseMapper.exists(new QueryWrapper<WareSkuEntity>()
                    .eq("sku_id", vo.getSkuId())
                    .eq("ware_id", vo.getWareId()));
            if (exists) {
                //更新记录
                this.baseMapper.addStock(vo);
            } else {
                //插入新纪录
                WareSkuEntity wareSku = new WareSkuEntity();
                wareSku.setSkuId(vo.getSkuId());
                wareSku.setWareId(vo.getWareId());
                wareSku.setStock(vo.getStock());
                wareSku.setStockLocked(0);
                wareSku.setSkuName(getSkuName(vo.getSkuId()));
                this.save(wareSku);
            }
        });
    }

    @Override
    public List<SkuHasStockTo> listHasStocksBySkuIds(List<Long> skuIds) {
        List<SkuStockVo> stockVos = this.getBaseMapper().selectUnlockedStocksBySkuIds(skuIds);
        return stockVos.stream().map(vo -> {
            SkuHasStockTo to = new SkuHasStockTo();
            to.setSkuId(vo.getSkuId());
            to.setHasStock(vo.getStock() > 0);
            return to;
        }).collect(Collectors.toList());

//        return skuIds.stream().map(skuId -> {
//            SkuHasStockTo vo = new SkuHasStockTo();
//            vo.setSkuId(skuId);
//
//            long stock = this.getBaseMapper().selectUnlockedStockBySkuId(skuId);
//            vo.setHasStock(stock > 0);
//
//            return vo;
//        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = NoEnoughStockException.class)
    @Override
    public List<LockStockResultVo> lockStockForOrder(WareSkuLockVo wareSkuLockVo) throws NoEnoughStockException {
        WareOrderTaskEntity orderTask = new WareOrderTaskEntity();
        orderTask.setOrderSn(wareSkuLockVo.getOrderSn());
        //保存工作单
        wareOrderTaskService.save(orderTask);

        List<SkuWaresVo> skuWaresVos = wareSkuLockVo.getItems().stream().map(item -> {
            SkuWaresVo skuWaresVo = new SkuWaresVo();
            skuWaresVo.setSkuId(item.getSkuId());
            skuWaresVo.setNum(item.getNum());
            skuWaresVo.setWareIds(this.baseMapper.listWareHasEnoughStock(item.getSkuId(), item.getNum()));
            return skuWaresVo;
        }).collect(Collectors.toList());

        WareOrderTaskTo wareOrderTaskTo = new WareOrderTaskTo();
        List<WareOrderTaskDetailEntity> taskDetails = new ArrayList<>();

        List<LockStockResultVo> results = skuWaresVos.stream().map(skuWares -> {
            Long skuId = skuWares.getSkuId();
            Integer num = skuWares.getNum();

            LockStockResultVo result = new LockStockResultVo();
            result.setSkuId(skuId);
            result.setNum(num);

            boolean locked = false;
            if (skuWares.getWareIds() == null) {
                throw new NoEnoughStockException("商品库存不足", skuId);
            }
            for (Long wareId : skuWares.getWareIds()) {
                Long count = this.baseMapper.lockStock(skuId, wareId, num);
                if (count > 0) {
                    locked = true;
                    result.setWareId(wareId);

                    //创建工作单详情
                    WareOrderTaskDetailEntity taskDetail = new WareOrderTaskDetailEntity();
                    taskDetail.setSkuId(skuId);
                    taskDetail.setWareId(wareId);
                    taskDetail.setSkuNum(num);
                    taskDetail.setTaskId(orderTask.getId());
                    taskDetail.setLockStatus(WareConstant.OrderTaskLockStatusEnum.LOCKED.getCode());
                    taskDetails.add(taskDetail);

                    break;
                }
            }
            if (!locked) {
                throw new NoEnoughStockException("商品库存不足", skuId);
            }

            return result;
        }).collect(Collectors.toList());

        //保存工作单详情
        wareOrderTaskDetailService.saveBatch(taskDetails);

        wareOrderTaskTo.setTaskId(orderTask.getId());
        wareOrderTaskTo.setDetailIds(
                taskDetails.stream().map(WareOrderTaskDetailEntity::getId).collect(Collectors.toList()));
        //通知MQ商品锁定成功
        sendLockedMessageToMq(wareOrderTaskTo);

        return results;
    }

    private void unlockStock(Long skuId, Long wareId, Integer skuNum) {
        this.baseMapper.unlockStock(skuId, wareId, skuNum);
    }

    private void sendLockedMessageToMq(WareOrderTaskTo wareOrderTaskTo) {
        rabbitTemplate.convertAndSend(WareConstant.MQ_STOCK_EVENT_EXCHANGE_NAME,
                WareConstant.MQ_STOCK_LOCKED_BINDING_ROUTING_KEY, wareOrderTaskTo);
    }

    @Override
    @Transactional
    public void releaseStockByOrderTaskId(Long taskId) {
        WareOrderTaskEntity orderTask = wareOrderTaskService.getById(taskId);
        if (orderTask != null) {
            releaseStockForOrderTask(orderTask);
        }
    }

    private void releaseStockForOrderTask(WareOrderTaskEntity orderTask) {
        String orderSn = orderTask.getOrderSn();
        R getOrderStatusR = orderFeignService.getOrderStatusByOrderSn(orderSn);
        Integer status = getOrderStatusR.get("data", Integer.class);
        if (getOrderStatusR.getCode() != 0) {
            throw new GetOrderStatusFailedException("远程查询订单状态失败");
        }
        if (status == null || status.equals(OrderStatusEnum.CANCELLED.getCode())) {
            //需要解锁库存
            _releaseStockForOrderTask(orderTask.getId());
        }
    }

    private void _releaseStockForOrderTask(Long taskId) {
        List<WareOrderTaskDetailEntity> taskDetails =
                wareOrderTaskDetailService.listLockedEntitiesByTaskId(taskId);
        if (CollectionUtils.isEmpty(taskDetails)) {
            return;
        }
        //解锁库存
        taskDetails.forEach(detail ->
                this.unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum())
        );
        List<WareOrderTaskDetailEntity> detailsForUpdate = taskDetails.stream().map(detail -> {
            WareOrderTaskDetailEntity detailForUpdate = new WareOrderTaskDetailEntity();
            detailForUpdate.setId(detail.getId());
            detailForUpdate.setLockStatus(WareConstant.OrderTaskLockStatusEnum.RELEASED.getCode());
            return detailForUpdate;
        }).collect(Collectors.toList());
        //修改detail的lockStatus
        wareOrderTaskDetailService.updateBatchById(detailsForUpdate);
    }

    @Override
    @Transactional
    public void releaseStockByOrderSn(String orderSn) {
        WareOrderTaskEntity orderTask = wareOrderTaskService.getByOrderSn(orderSn);
        if (orderTask != null) {
            releaseStockForOrderTask(orderTask);
        }
    }

    public static class GetOrderStatusFailedException extends RuntimeException {
        public GetOrderStatusFailedException(String msg) {
            super(msg);
        }
    }
}