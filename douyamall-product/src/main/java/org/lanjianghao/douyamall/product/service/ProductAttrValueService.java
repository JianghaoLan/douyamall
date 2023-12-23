package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.ProductAttrValueEntity;
import org.lanjianghao.douyamall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> listAttrValuesBySpuId(Long spuId);

    void updateAttrValuesBySpuId(Long spuId, List<ProductAttrValueEntity> attrValues);
}

