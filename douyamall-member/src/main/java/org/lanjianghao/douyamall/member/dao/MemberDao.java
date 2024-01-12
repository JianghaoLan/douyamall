package org.lanjianghao.douyamall.member.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lanjianghao.douyamall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:30:22
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    @Select("SELECT `integration` FROM `ums_member` WHERE id=#{id}")
    Integer selectIntegrationById(@Param("id") Long id);
}
