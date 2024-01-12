package org.lanjianghao.douyamall.order.interceptor;


import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (new AntPathMatcher().match("/order/order/ordersn/*/status", request.getRequestURI())) {
            return true;
        }

        HttpSession session = request.getSession();

        MemberVo loginUser = (MemberVo)session.getAttribute(AuthConstant.LOGIN_USER_SESSION_KEY);
        if (loginUser != null) {
            AuthInterceptor.loginUser.set(loginUser);
            return true;
        }

        response.sendRedirect("http://auth.douyamall.com/login.html");
        return false;
    }
}
