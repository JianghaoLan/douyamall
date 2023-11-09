package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.lanjianghao.douyamall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 属性&属性分组关联
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelations(@Param("entities") AttrAttrgroupRelationEntity[] relationEntities);
}
