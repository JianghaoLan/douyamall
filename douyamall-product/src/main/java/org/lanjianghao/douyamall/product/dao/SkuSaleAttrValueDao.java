package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lanjianghao.douyamall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lanjianghao.douyamall.product.vo.Attr;
import org.lanjianghao.douyamall.product.vo.SkuSaleAttr;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuSaleAttr> selectSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
