package org.lanjianghao.douyamall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.lanjianghao.douyamall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu信息
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    @Update("UPDATE `pms_spu_info` SET publish_status = #{status}, update_time = NOW() WHERE id = #{spuId}")
    void updatePublishStatus(@Param("spuId") Long spuId, @Param("status") int status);
}
