package com.hk.controller;

import com.hk.constant.SessionProperty;
import com.hk.service.EditorService;
import com.hk.service.NovelService;
import com.hk.util.ResultUtil;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

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
    @PostMapping("/recommendNovel")
    public ModelAndView recommendNovel(@RequestParam Map<String, String> params, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer novelId = Integer.valueOf(params.get("novel_id"));
        String reason = params.get("reason");
        Integer editorId = (Integer)session.getAttribute(SessionProperty.EDITOR_LOGIN_EDITOR_ID);
        editorService.recommendNovel(novelId, editorId, reason);
        modelAndView.setViewName("redirect:/editor/workSpacePage");
        modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
        return modelAndView;
    }
}
