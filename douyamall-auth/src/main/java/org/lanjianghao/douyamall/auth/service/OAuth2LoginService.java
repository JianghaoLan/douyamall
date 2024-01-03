package org.lanjianghao.douyamall.auth.service;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.auth.exception.GetWeiboAccessTokenFailedException;
import org.springframework.beans.factory.annotation.Autowired;

public interface OAuth2LoginService {
    MemberVo weiboLogin(String code) throws GetWeiboAccessTokenFailedException;
}
