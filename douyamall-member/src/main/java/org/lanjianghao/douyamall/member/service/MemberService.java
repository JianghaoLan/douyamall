package org.lanjianghao.douyamall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.member.entity.MemberEntity;
import org.lanjianghao.douyamall.member.exception.MobileExistsException;
import org.lanjianghao.douyamall.member.exception.UsernameExistsException;
import org.lanjianghao.douyamall.member.vo.MemberLoginVo;
import org.lanjianghao.douyamall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:30:22
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo) throws MobileExistsException, UsernameExistsException;

    MemberEntity login(MemberLoginVo vo);
}

