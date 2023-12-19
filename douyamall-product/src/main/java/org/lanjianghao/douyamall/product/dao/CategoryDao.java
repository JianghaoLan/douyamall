package org.lanjianghao.douyamall.product.dao;

import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
