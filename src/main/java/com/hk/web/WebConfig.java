package com.hk.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
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

        registry.addViewController("/creator/creatorRegister").setViewName("/creator/creatorRegister");
        registry.addViewController("/editor/editorCenter").setViewName("/editor/editorCenter");
        registry.addViewController("/editor/editorLogin").setViewName("/editor/editorLogin");


        registry.addViewController("/reader/readerLogin").setViewName("/reader/readerLogin");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");

    }
}
