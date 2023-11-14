package org.lanjianghao.douyamall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.ware.entity.PurchaseEntity;
import org.lanjianghao.douyamall.ware.exception.IncorrectPurchaseItemsException;
import org.lanjianghao.douyamall.ware.exception.IncorrectPurchaseStatusException;
import org.lanjianghao.douyamall.ware.vo.CompletePurchaseVo;
import org.lanjianghao.douyamall.ware.vo.MergePurchaseVo;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 16:05:10
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivedPage(Map<String, Object> params);

    void mergePurchase(MergePurchaseVo mergeVo);

    void receive(List<Long> purchaseIds);

    void completePurchase(CompletePurchaseVo vo)
            throws IncorrectPurchaseItemsException, IncorrectPurchaseStatusException;
}
