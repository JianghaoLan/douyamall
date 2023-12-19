package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.AttrEntity;
import org.lanjianghao.douyamall.product.vo.AttrNameVo;
import org.lanjianghao.douyamall.product.vo.AttrRespVo;
import org.lanjianghao.douyamall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId, String attrType);

    void saveAttr(AttrVo attr);

    AttrRespVo getAttrInfoById(Long attrId);

    void updateAttrById(AttrVo attr);

    List<AttrEntity> listRelatedAttrs(Long attrgroupId);

    PageUtils queryNoRelationAttrsPage(Map<String, Object> params, Long attrgroupId);

    List<AttrNameVo> listAttrNames(List<Long> attrIds);
}

