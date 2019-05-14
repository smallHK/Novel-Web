package com.hk.controller;

import com.hk.constant.SessionProperty;
import com.hk.service.EditorService;
import com.hk.service.NovelService;
import com.hk.util.ResultUtil;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * @author smallHK
 * 2019/5/10 22:16
 */
@Controller
@RequestMapping("/editor")
public class EditorRedirectController {

    private NovelService novelService;

    private EditorService editorService;

    public EditorRedirectController(NovelService novelService,
                                    EditorService editorService) {
        this.novelService = novelService;
        this.editorService = editorService;
    }

    /**
     * 同意卷发布
     */
    @GetMapping("/passVolumePublishEvent/{eventId}")
    public ModelAndView agreeVolumePublish(@PathVariable  Integer eventId) {
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
    public ModelAndView agreeChapterPublish(@PathVariable Integer eventId) {
        ModelAndView modelAndView = new ModelAndView();
        novelService.agreeChapterPublish(eventId);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        return modelAndView;
    }

    /**
     * 推荐封面小说
     */
    @GetMapping("/recommendNovel/{novelId}")
    public ModelAndView recommendNovel(@PathVariable Integer novelId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer editorId = (Integer)session.getAttribute(SessionProperty.EDITOR_LOGIN_EDITOR_ID);
        editorService.recommendNovel(novelId, editorId);
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
        return modelAndView;
    }
}
