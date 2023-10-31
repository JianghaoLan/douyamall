package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.lanjianghao.douyamall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌分类关联
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
	public boolean updateBrand(@Param("brandId") Long brandId, @Param("brandName") String brandName);

	public boolean updateCategory(@Param("catelogId") Long catelogId, @Param("catelogName") String catelogName);
}
