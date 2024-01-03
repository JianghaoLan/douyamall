package org.lanjianghao.douyamall.auth.service;

import org.lanjianghao.douyamall.auth.exception.GetWeiboAccessTokenFailedException;
import org.lanjianghao.douyamall.auth.exception.GetWeiboUserFailedException;
import org.lanjianghao.douyamall.auth.to.WeiboAccessTokenRespTo;
import org.lanjianghao.douyamall.auth.to.WeiboUserTo;

public interface WeiboOpenService {

    WeiboAccessTokenRespTo getAccessToken(String code) throws GetWeiboAccessTokenFailedException;

    WeiboUserTo getUser(String accessToken, String uid) throws GetWeiboUserFailedException;
}
