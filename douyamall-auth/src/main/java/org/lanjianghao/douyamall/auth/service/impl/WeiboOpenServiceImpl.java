package org.lanjianghao.douyamall.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.douyamall.auth.exception.GetWeiboAccessTokenFailedException;
import org.lanjianghao.douyamall.auth.exception.GetWeiboUserFailedException;
import org.lanjianghao.douyamall.auth.service.WeiboOpenService;
import org.lanjianghao.douyamall.auth.to.WeiboAccessTokenRespTo;
import org.lanjianghao.douyamall.auth.to.WeiboUserTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class WeiboOpenServiceImpl implements WeiboOpenService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    public WeiboAccessTokenRespTo getAccessToken(String code) throws GetWeiboAccessTokenFailedException {
        URI uri;
        try {
            uri = new URIBuilder(AuthConstant.WeiboOpenConstant.OAUTH2_URL)
                    .addParameter("client_id", AuthConstant.WeiboOpenConstant.OAUTH2_CLIENT_ID)
                    .addParameter("client_secret", AuthConstant.WeiboOpenConstant.OAUTH2_CLIENT_SECRET)
                    .addParameter("grant_type", "authorization_code")
                    .addParameter("redirect_uri", AuthConstant.WeiboOpenConstant.OAUTH2_REDIRECT_URI)
                    .addParameter("code", code)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<WeiboAccessTokenRespTo> respEnt;
        try {
            respEnt = restTemplate.postForEntity(uri, null, WeiboAccessTokenRespTo.class);
        } catch (Exception e) { throw new GetWeiboAccessTokenFailedException("获取微博Access Token失败"); }

        if (respEnt.getStatusCodeValue() != 200) {
            throw new GetWeiboAccessTokenFailedException("获取微博Access Token失败");
        }
        return respEnt.getBody();
    }

    //https://open.weibo.com/wiki/2/users/show
    @Override
    public WeiboUserTo getUser(String accessToken, String uid) throws GetWeiboUserFailedException {
        URI uri;
        try {
            uri = new URIBuilder(AuthConstant.WeiboOpenConstant.USER_URL)
                    .addParameter("access_token", accessToken)
                    .addParameter("uid", uid)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<WeiboUserTo> resp;
        try {
            resp = restTemplate.getForEntity(uri, WeiboUserTo.class);
        } catch (Exception e) { throw new GetWeiboUserFailedException("获取微博用户信息失败"); }

        if (resp.getStatusCodeValue() != 200) {
            throw new GetWeiboUserFailedException("获取微博用户信息失败");
        }
        return resp.getBody();
    }
}
