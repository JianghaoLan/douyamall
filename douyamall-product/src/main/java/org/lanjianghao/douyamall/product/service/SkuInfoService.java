package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.SkuInfoEntity;
import org.lanjianghao.douyamall.product.vo.SkuItemVo;
import org.lanjianghao.douyamall.product.vo.SkuPriceVo;
import org.lanjianghao.douyamall.product.to.SkuSpuIdTo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVo getItem(Long skuId);

    List<SkuPriceVo> listPricesBySkuIds(List<Long> skuIds);

//    Long getSpuIdBySkuId(Long skuId);

    List<SkuSpuIdTo> getSpuIdsBySkuIds(List<Long> skuIds);
}

