package org.lanjianghao.douyamall.auth.service.impl;

import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.constant.MemberConstant;
import org.lanjianghao.common.exception.BizCodeEnum;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.auth.exception.GetWeiboAccessTokenFailedException;
import org.lanjianghao.douyamall.auth.exception.GetWeiboUserFailedException;
import org.lanjianghao.douyamall.auth.exception.OAuth2LoginFailedException;
import org.lanjianghao.douyamall.auth.feign.MemberFeignService;
import org.lanjianghao.douyamall.auth.service.OAuth2LoginService;
import org.lanjianghao.douyamall.auth.service.WeiboOpenService;
import org.lanjianghao.douyamall.auth.to.WeiboAccessTokenRespTo;
import org.lanjianghao.douyamall.auth.to.WeiboUserTo;
import org.lanjianghao.douyamall.auth.vo.OAuth2LoginVo;
import org.lanjianghao.douyamall.auth.vo.OAuth2RegisterVo;
import org.lanjianghao.douyamall.auth.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuth2LoginServiceImpl implements OAuth2LoginService {
    @Autowired
    private WeiboOpenService weiboOpenService;

    @Autowired
    private MemberFeignService memberFeignService;

    private Integer getWeiboUserIntGender(String strGender) {
        switch (strGender) {
            case "m":
                return MemberConstant.MemberGenderEnum.MALE.getCode();
            case "f":
                return MemberConstant.MemberGenderEnum.FEMALE.getCode();
            case "n":
                return MemberConstant.MemberGenderEnum.UNKNOWN.getCode();
            default:
                return null;
        }
    }

    @Override
    public MemberVo weiboLogin(String code) throws GetWeiboAccessTokenFailedException {

        WeiboAccessTokenRespTo accessTokenTo = weiboOpenService.getAccessToken(code);

        OAuth2LoginVo oAuth2LoginVo = new OAuth2LoginVo();
        oAuth2LoginVo.setPlatform(AuthConstant.OAuth2PlatformEnum.WEIBO.getCode());
        oAuth2LoginVo.setUid(accessTokenTo.getUid());
        oAuth2LoginVo.setAccessToken(accessTokenTo.getAccessToken());
        oAuth2LoginVo.setExpiresIn(accessTokenTo.getExpiresIn());

        R tryLoginR;
        try {
            tryLoginR = memberFeignService.oAuth2Login(oAuth2LoginVo);
        } catch (Exception e) {
            throw new OAuth2LoginFailedException();
        }

        if (tryLoginR.getCode() == 0) {
            return tryLoginR.get("data", MemberVo.class);
        }

        //用户不存在，注册
        if (tryLoginR.getCode() == BizCodeEnum.OAUTH2_USER_NOT_EXISTS_EXCEPTION.getCode()) {
            WeiboUserTo userTo;
            try {
                userTo = weiboOpenService.getUser(accessTokenTo.getAccessToken(), accessTokenTo.getUid());
            } catch (GetWeiboUserFailedException e) {
                userTo = new WeiboUserTo();
            }

            OAuth2RegisterVo regVo = new OAuth2RegisterVo();

            regVo.setAccessToken(accessTokenTo.getAccessToken());
            regVo.setExpiresIn(accessTokenTo.getExpiresIn());
            regVo.setOauth2Platform(AuthConstant.OAuth2PlatformEnum.WEIBO.getCode());
            regVo.setSocialUid(userTo.getId());
            regVo.setGender(getWeiboUserIntGender(userTo.getGender()));
            regVo.setHeader(userTo.getProfileImageUrl());
            regVo.setNickname(userTo.getScreenName());

            memberFeignService.oAuth2Register(regVo);

            R loginR;
            try {
                loginR = memberFeignService.oAuth2Login(oAuth2LoginVo);
            } catch (Exception e) {
                throw new OAuth2LoginFailedException();
            }

            if (loginR.getCode() == 0) {
                return loginR.get("data", MemberVo.class);
            }
        }

        throw new OAuth2LoginFailedException();
    }
}
