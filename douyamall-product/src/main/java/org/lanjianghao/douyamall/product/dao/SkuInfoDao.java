package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.lanjianghao.douyamall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lanjianghao.douyamall.product.vo.SkuPriceVo;
import org.lanjianghao.douyamall.product.to.SkuSpuIdTo;

import java.util.List;

/**
 * sku信息
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    List<SkuPriceVo> selectPricesBySkuIds(@Param("skuIds") List<Long> skuIds);

//    @Select("SELECT `spu_id` FROM `pms_sku_info` WHERE `sku_id` = #{skuId}")
//    Long getSpuIdBySkuId(@Param("skuId") Long skuId);

    List<SkuSpuIdTo> getSpuIdsBySkuIds(@Param("skuIds") List<Long> skuIds);
}
