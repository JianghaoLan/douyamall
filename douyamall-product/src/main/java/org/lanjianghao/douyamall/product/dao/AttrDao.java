package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lanjianghao.douyamall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lanjianghao.douyamall.product.vo.AttrNameVo;

import java.util.List;

/**
 * 商品属性
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<AttrNameVo> selectAttrNamesByIds(@Param("attrIds") List<Long> attrIds);
}
