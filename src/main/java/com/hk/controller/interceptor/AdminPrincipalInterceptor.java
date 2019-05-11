package com.hk.controller.interceptor;

import com.hk.constant.SessionProperty;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author smallHK
 * 2019/5/8 22:12
 */
public class AdminPrincipalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        //处理创作者
        String penname = (String) session.getAttribute(SessionProperty.ROOT_LOGIN_NAME);
        if (Objects.nonNull(penname)) {
            return true;
        } else {
            response.sendRedirect("/adminLogin");
            return false;
        }
    }

}
