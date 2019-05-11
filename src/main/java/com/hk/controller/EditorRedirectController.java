package com.hk.controller;

import com.hk.service.NovelService;
import com.hk.util.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author smallHK
 * 2019/5/10 22:16
 */
public class EditorRedirectController {

    private NovelService novelService;

    public EditorRedirectController(NovelService novelService) {
        this.novelService = novelService;
    }

    /**
     * 同意卷发布
     */
    @GetMapping("/passVolumePublishEvent/{eventId}")
    public ModelAndView agreeVolumePublish(Integer eventId) {
        ModelAndView modelAndView = new ModelAndView();
        novelService.agreeVolumePublish(eventId);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        return modelAndView;
    }

    /**
     * 同意章节发布
     */
    @GetMapping("/passChapterPublishEvent/{eventId}")
    public ModelAndView agreeChapterPublish(Integer eventId) {
        ModelAndView modelAndView = new ModelAndView();
        novelService.agreeChapterPublish(eventId);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        return modelAndView;
    }
}
