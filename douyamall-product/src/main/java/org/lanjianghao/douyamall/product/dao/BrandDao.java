package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.lanjianghao.douyamall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lanjianghao.douyamall.product.vo.BrandNameVo;

import java.util.List;

/**
 * 品牌
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    List<BrandNameVo> selectBrandNames(@Param("brandIds") List<Long> brandIds);
}
