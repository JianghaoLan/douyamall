package org.lanjianghao.douyamall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.douyamall.ware.exception.IncorrectPurchaseItemsException;
import org.lanjianghao.douyamall.ware.exception.IncorrectPurchaseStatusException;
import org.lanjianghao.douyamall.ware.vo.CompletePurchaseVo;
import org.lanjianghao.douyamall.ware.vo.MergePurchaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.ware.entity.PurchaseEntity;
import org.lanjianghao.douyamall.ware.service.PurchaseService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;

import javax.validation.Valid;


/**
 * 采购信息
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 16:05:10
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 累出未接收的订单
     */
    @RequestMapping("/unreceive/list")
    public R listUnreceived(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceivedPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/merge")
    public R merge(@RequestBody MergePurchaseVo mergeVo){
        purchaseService.mergePurchase(mergeVo);

        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchase.setStatus(WareConstant.PurchaseEnum.PURCHASE_STATUS_CREATED.getCode());
		purchaseService.save(purchase);

        return R.ok();
    }

    @PostMapping("/received")
    public R receive(@RequestBody List<Long> purchaseIds) {
        purchaseService.receive(purchaseIds);

        return R.ok();
    }

    @PostMapping("/done")
    public R completePurchase(@RequestBody @Validated CompletePurchaseVo vo)
            throws IncorrectPurchaseItemsException, IncorrectPurchaseStatusException {
        purchaseService.completePurchase(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
