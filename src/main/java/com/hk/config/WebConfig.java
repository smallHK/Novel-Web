package com.hk.config;

import com.hk.web.CreatorPrincipalInterceptor;
import com.hk.web.EditorPrincipalInterceptor;
import com.hk.web.ReaderPrincipalInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author smallHK
 * 2019/4/12 9:00
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
        registry.addViewController("/index").setViewName("/index");

        registry.addViewController("/creator/registerPage").setViewName("/creator/registerPage");
        registry.addViewController("/creator/loginPage").setViewName("/creator/loginPage");
        registry.addViewController("/creator/novelManagePage").setViewName("/creator/novelManagePage");
        registry.addViewController("/creator/createNovelPage").setViewName("/creator/addNovelPage");

        registry.addViewController("/editor/editorCenter").setViewName("/editor/editorCenter");
        registry.addViewController("/editor/loginPage").setViewName("/editor/loginPage");


        registry.addViewController("/reader/readerLogin").setViewName("/reader/loginPage");
        registry.addViewController("/reader/registerPage").setViewName("/reader/registerPage");

        registry.addViewController("/visitor/novelMain").setViewName("/visitor/novelMainPage");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CreatorPrincipalInterceptor())
                .addPathPatterns("/creator/**")
                .excludePathPatterns("/creator/login")
                .excludePathPatterns("/creator/loginPage")
                .excludePathPatterns("/creator/register")
                .excludePathPatterns("/creator/registerPage");


        registry.addInterceptor(new EditorPrincipalInterceptor())
                .addPathPatterns("/editor/**")
                .excludePathPatterns("/editor/login")
                .excludePathPatterns("/editor/loginPage")
                .excludePathPatterns("/editor/register")
                .excludePathPatterns("/editor/registerPage");

        registry.addInterceptor(new ReaderPrincipalInterceptor())
                .addPathPatterns("/reader/**")
                .excludePathPatterns("/reader/login")
                .excludePathPatterns("/reader/loginPage")
                .excludePathPatterns("/reader/register")
                .excludePathPatterns("/reader/registerPage");

    }
}
