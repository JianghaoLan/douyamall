package org.lanjianghao.douyamall.ware.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lanjianghao.douyamall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lanjianghao.douyamall.ware.vo.AddStockVo;
import org.lanjianghao.douyamall.ware.vo.SkuStockVo;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 16:05:10
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	int addStock(@Param("addStockVo") AddStockVo addStockVo);

    @Select("SELECT SUM(sku_id - stock_locked) FROM wms_ware_sku WHERE sku_id = #{skuId}")
    long selectUnlockedStockBySkuId(@Param("skuId") Long skuId);

    List<SkuStockVo> selectUnlockedStocksBySkuIds(@Param("skuIds") List<Long> skuIds);
}
