package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.SkuSaleAttrValueEntity;
import org.lanjianghao.douyamall.product.vo.SkuSaleAttr;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuSaleAttrValueEntity> listBySkuId(Long skuId);

    List<SkuSaleAttr> listSaleAttrBySpuId(Long spuId);
}

