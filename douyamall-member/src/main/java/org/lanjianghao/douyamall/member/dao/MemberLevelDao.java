package org.lanjianghao.douyamall.member.dao;

import org.apache.ibatis.annotations.Select;
import org.lanjianghao.common.constant.MemberConstant;
import org.lanjianghao.douyamall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:30:22
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {
    @Select("SELECT `id` FROM `ums_member_level` WHERE default_status = 1")
    Long selectDefaultLevelId();
}
