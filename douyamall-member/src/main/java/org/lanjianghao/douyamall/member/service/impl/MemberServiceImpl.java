package org.lanjianghao.douyamall.member.service.impl;

import org.lanjianghao.douyamall.member.exception.MobileExistsException;
import org.lanjianghao.douyamall.member.exception.UsernameExistsException;
import org.lanjianghao.douyamall.member.service.MemberLevelService;
import org.lanjianghao.douyamall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.member.dao.MemberDao;
import org.lanjianghao.douyamall.member.entity.MemberEntity;
import org.lanjianghao.douyamall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    private void checkMobileNotExists(String phone) throws MobileExistsException {
        long count = this.count(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0) {
            throw new MobileExistsException();
        }
    }

    private void checkUsernameNotExists(String username) throws UsernameExistsException {
        long count = this.count(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UsernameExistsException();
        }
    }

    @Override
    public void register(MemberRegisterVo vo) {
        checkMobileNotExists(vo.getPhone());
        checkUsernameNotExists(vo.getPassword());

        MemberEntity entity = new MemberEntity();
        entity.setLevelId(memberLevelService.getDefaultLevelId());
        entity.setUsername(vo.getUserName());
        entity.setMobile(vo.getPhone());

        //password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(passwordEncoder.encode(vo.getPassword()));

        this.save(entity);
    }

}