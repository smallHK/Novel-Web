package com.hk.controller.interceptor;

import com.hk.util.SessionProperty;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 未登录读者拦截器
 *
 * @author smallHK
 * 2019/4/22 19:59
 */
public class ReaderPrincipalInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //处理创作者
        String editorName = (String) session.getAttribute(SessionProperty.READER_LOGIN_READER_NAME);
        if (Objects.nonNull(editorName)) {
            return true;
        } else {
            response.sendRedirect("/reader/loginPage");
            return false;
        }
    }
}
