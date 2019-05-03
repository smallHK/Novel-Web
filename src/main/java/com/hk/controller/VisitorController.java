package com.hk.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Creator;
import com.hk.entity.Novel;
import com.hk.entity.NovelComment;
import com.hk.po.ChapterInfo;
import com.hk.po.NovelCommentInfo;
import com.hk.po.NovelIndex;
import com.hk.po.NovelInfo;
import com.hk.repository.CreatorRepository;
import com.hk.repository.NovelCommentRepo;
import com.hk.repository.NovelRepository;
import com.hk.service.NovelService;
import com.hk.util.EntityStatus;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 无帐户游客调用的接口
 *
 * @author smallHK
 * 2019/4/15 9:01
 */
@Controller
@RequestMapping("/visitor")
public class VisitorController {


    private NovelCommentRepo novelCommentRepo;

    private NovelService novelService;

    public VisitorController(NovelCommentRepo novelCommentRepo, NovelService novelService) {
        this.novelCommentRepo = novelCommentRepo;
        this.novelService = novelService;
    }

    /**
     * 装填游客主页
     */
    @GetMapping("/novelMain")
    public ModelAndView novelMainPage() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("/visitor/novelMainPage");
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));

        //获取推荐页小说

        //获取热门小说

        //获取新开放小说
        return modelAndView;
    }


    /**
     * 装填搜索页
     */
    @PostMapping("/searchNovel")
    public ModelAndView searchNovel(@RequestParam Map<String, String> param) {
        ModelAndView modelAndView = new ModelAndView();

        String keyword = param.get("keyword");

        //分页搜索小说
        List<Novel> novelList = novelService.findNovelListByKeyword(keyword);

        modelAndView.addObject("novelList", novelList);
        modelAndView.setViewName("/visitor/searchPage");

        return modelAndView;
    }

    /**
     * 装填小说信息页
     */
    @GetMapping("/novelInfo/{novelId}")
    public ModelAndView findNovelInfoPage(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        NovelInfo novelInfo = novelService.findNovelInfo(novelId);
        List<NovelCommentInfo> infoList = novelService.listAllNovelCommentInfo(novelId);
        modelAndView.setViewName("/visitor/novelInfoPage");
        modelAndView.addObject("commentInfoList", infoList);
        modelAndView.addObject("novelInfo", novelInfo);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }


    /**
     * 装填小说目录页
     */
    @GetMapping("/novelIndex/{novelId}")
    public ModelAndView getPublishedNovelIndex(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/visitor/NovelIndex");
        NovelIndex index = novelService.findNovelIndex(novelId);
        modelAndView.addObject("novelIndexInfo", index);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;

    }

    /**
     * 装填章节
     *
     * 会增加点击量
     */
    @GetMapping("/novelChapter/{chapterId}")
    public ModelAndView specialChapterPage(@PathVariable Integer chapterId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/visitor/contentPage");
        ChapterInfo chapterInfo = novelService.findNovelChapter(chapterId);
        novelService.addClickNum(chapterId);
        modelAndView.addObject("chapterInfo", chapterInfo);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));

        return modelAndView;
    }

}
