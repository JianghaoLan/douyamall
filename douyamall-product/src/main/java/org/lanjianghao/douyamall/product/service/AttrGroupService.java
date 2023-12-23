package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.AttrGroupEntity;
import org.lanjianghao.douyamall.product.vo.AttrGroupWithAttrsVo;
import org.lanjianghao.douyamall.product.vo.SpuBaseAttrGroup;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> listAttrGroupsWithAttrsByCatelogId(Long catId);

    List<SpuBaseAttrGroup> listAttrGroupBySpuId(Long spuId);
}

