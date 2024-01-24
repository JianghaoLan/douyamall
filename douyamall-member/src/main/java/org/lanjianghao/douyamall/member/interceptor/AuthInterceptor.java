package org.lanjianghao.douyamall.member.interceptor;


import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String[] whiteList = {
                "/member/member/login",
                "/member/member/oauth2/**",
                "/member/member/register",
                "/member/memberreceiveaddress/info/**"
        };
        for (String pattern : whiteList) {
            if (new AntPathMatcher().match(pattern, request.getRequestURI())) {
                return true;
            }
        }

        HttpSession session = request.getSession();

        MemberVo loginUser = (MemberVo)session.getAttribute(AuthConstant.LOGIN_USER_SESSION_KEY);
        if (loginUser == null) {
            response.sendRedirect("http://auth.douyamall.com/login.html");
            return false;
        }
        AuthInterceptor.loginUser.set(loginUser);
        return true;
    }
}
