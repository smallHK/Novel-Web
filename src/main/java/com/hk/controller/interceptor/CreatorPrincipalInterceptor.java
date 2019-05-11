package com.hk.controller.interceptor;

import com.hk.constant.SessionProperty;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 *
 * 用于禁止未登录用户访问数据页面
 *
 * @author smallHK
 * 2019/4/21 22:57
 */
public class CreatorPrincipalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        //处理创作者
        String penname = (String) session.getAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_NAME);
        if (Objects.nonNull(penname)) {
            return true;
        } else {
            response.sendRedirect("/creator/creatorRegister");
            return false;
        }
    }
}
