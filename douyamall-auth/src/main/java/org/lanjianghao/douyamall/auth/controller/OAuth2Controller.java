package org.lanjianghao.douyamall.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.auth.exception.GetWeiboAccessTokenFailedException;
import org.lanjianghao.douyamall.auth.service.OAuth2LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 第三方登录
 */
@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @Autowired
    OAuth2LoginService oAuth2LoginService;

    @GetMapping("/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) {
        try {
            MemberVo member = oAuth2LoginService.weiboLogin(code);
            session.setAttribute(AuthConstant.LOGIN_USER_SESSION_KEY, member);
            log.debug("登录成功。用户信息：{}", member.toString());
        } catch (Exception e) {
//            throw new RuntimeException(e);
            return "redirect:http://auth.douyamall.com/login.html";
        }

        return "redirect:http://douyamall.com";
    }
}
