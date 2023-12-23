package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lanjianghao.douyamall.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lanjianghao.douyamall.product.vo.SkuItemVo;

import java.util.List;

/**
 * spu属性值
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

//    @Select("SELECT attr_name, attr_value FROM `pms_product_attr_value` WHERE spuId = #{spuId}")
//    List<SkuItemVo.SpuBaseAttr> selectAttrGroupBySpuId(@Param("spuId") Long spuId);
}
