package org.lanjianghao.douyamall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.to.OrderTo;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.ware.entity.WareSkuEntity;
import org.lanjianghao.douyamall.ware.vo.AddStockVo;
import org.lanjianghao.common.to.SkuHasStockTo;
import org.lanjianghao.douyamall.ware.vo.LockStockResultVo;
import org.lanjianghao.douyamall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 16:05:10
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStocks(List<AddStockVo> addStockVos);

    List<SkuHasStockTo> listHasStocksBySkuIds(List<Long> skuIds);

    List<LockStockResultVo> lockStockForOrder(WareSkuLockVo wareSkuLockVo);

    void releaseStockByOrderTaskId(Long taskId);

    void releaseStockByOrderSn(String orderSn);
}

