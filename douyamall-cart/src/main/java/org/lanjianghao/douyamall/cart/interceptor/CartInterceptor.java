package org.lanjianghao.douyamall.cart.interceptor;

import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lanjianghao.common.constant.AuthConstant;
import org.lanjianghao.common.constant.CartConstant;
import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.cart.to.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.UUID;

/**
 * 判断用户登录状态，并封装传递给Controller
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    private String getTempUserKey(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String generateTempUserKey() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        UserInfoTo userInfo = new UserInfoTo();

        HttpSession session = request.getSession();
        MemberVo member = (MemberVo) session.getAttribute(AuthConstant.LOGIN_USER_SESSION_KEY);
        if (member != null) {
            userInfo.setUserId(member.getId());
        }

        String userKey = getTempUserKey(request);
        //如果没有临时用户，则分配一个临时用户
        if (userKey == null) {
            userKey = generateTempUserKey();

            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userKey);
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            cookie.setDomain("douyamall.com");
            response.addCookie(cookie);
        }
        userInfo.setUserKey(userKey);

        threadLocal.set(userInfo);

        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler,
                           ModelAndView modelAndView) throws Exception {

//        response.addCookie();
    }
}
