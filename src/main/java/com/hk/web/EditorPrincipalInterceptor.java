package com.hk.web;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 未登录编辑拦截器
 *
 * @author smallHK
 * 2019/4/22 19:50
 */
public class EditorPrincipalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //处理创作者
        String editorName = (String) session.getAttribute("login_editor_name");
        if (Objects.nonNull(editorName)) {
            return true;
        } else {
            response.sendRedirect("/editor/editorLogin");
            return false;
        }
    }
}
