package org.lanjianghao.douyamall.member.service.impl;

import org.lanjianghao.douyamall.member.exception.MobileExistsException;
import org.lanjianghao.douyamall.member.exception.UsernameExistsException;
import org.lanjianghao.douyamall.member.service.MemberLevelService;
import org.lanjianghao.douyamall.member.vo.MemberLoginVo;
import org.lanjianghao.douyamall.member.vo.MemberRegisterVo;
import org.lanjianghao.douyamall.member.vo.OAuth2LoginVo;
import org.lanjianghao.douyamall.member.vo.OAuth2RegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        entity.setCreateTime(new Date());

        //password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        entity.setPassword(passwordEncoder.encode(vo.getPassword()));

        this.save(entity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity member = this.getOne(new QueryWrapper<MemberEntity>()
                .eq("username", vo.getAccount())
                .or().eq("mobile", vo.getAccount()));

        if (member == null) {
            return null;
        }
        //password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(vo.getPassword(), member.getPassword());
        if (matches) {
            return member;
        }
        return null;
    }

    private MemberEntity getOAuth2MemberByPlatformAndUid(Integer platform, String uid) {
        return this.baseMapper.selectOne(
                new QueryWrapper<MemberEntity>()
                        .eq("oauth2_platform", platform)
                        .eq("social_uid", uid));
    }

    @Override
    public void oAuth2Register(OAuth2RegisterVo vo) {
        MemberEntity entity = new MemberEntity();
        BeanUtils.copyProperties(vo, entity);

        entity.setLevelId(memberLevelService.getDefaultLevelId());
        entity.setCreateTime(new Date());

        this.save(entity);
    }

    @Override
    public MemberEntity oAuth2Login(OAuth2LoginVo vo) {
        MemberEntity member = getOAuth2MemberByPlatformAndUid(vo.getPlatform(), vo.getUid());
        if (member != null) {
            //社交账号已经绑定本站账号，更新过期时间
            MemberEntity update = new MemberEntity();
            update.setId(member.getId());
            update.setAccessToken(vo.getAccessToken());
            update.setExpiresIn(vo.getExpiresIn());
            this.updateById(update);

            member.setAccessToken(vo.getAccessToken());
            member.setExpiresIn(vo.getExpiresIn());
            return member;
        }

        //该社交账号未绑定账号
        return null;
    }

}