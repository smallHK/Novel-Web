package com.hk.controller;

import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Creator;
import com.hk.entity.Novel;
import com.hk.entity.Volume;
import com.hk.po.ChapterInfo;
import com.hk.po.NovelIndex;
import com.hk.po.NovelInfo;
import com.hk.po.VolumeInfo;
import com.hk.repository.CreatorRepository;
import com.hk.service.NovelService;
import com.hk.util.EntityStatus;
import com.hk.util.ResultUtil;
import com.hk.util.SessionProperty;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.hk.util.ResultUtil.success;

/**
 * smallHK
 * 2019/3/26 16:55
 * <p>
 * 为作者提供业务服务
 */
@Controller
@RequestMapping(path = "/creator")
public class CreatorController {

    private NovelService novelService;

    private CreatorRepository creatorRepository;


    public CreatorController(CreatorRepository creatorRepository,
                             NovelService novelService) {
        this.creatorRepository = creatorRepository;
        this.novelService = novelService;
    }


    /**
     * 登陆功能
     * 登陆后并不会进行页面跳转，所以返回json
     * 已登录状态，不会具有登陆页面
     *
     * @param params 前台传入的参数
     * @return 返回对登陆状态的判断
     */
    @PostMapping(path = "/login")
    public ModelAndView login(@RequestParam Map<String, String> params, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        try {
            Iterable<Creator> creatorList = creatorRepository.findAll();
            String penname = params.get("penname");
            String password = params.get("password");
            for (Creator creator : creatorList) {
                if (creator.getPenName().equals(penname) && creator.getPassword().equals(password)) {
                    session.setAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_NAME, penname);
                    session.setAttribute(SessionProperty.CREATOR_LOGIN_CREATOR_ID, creator.getId());
                    modelAndView.setViewName("redirect:/creator/novelManagePage");
                    modelAndView.addObject("resultInfo", success("读者登陆成功！"));
                    return modelAndView;
                }
            }
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("读者登陆失败！"));
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();

            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure(e.getMessage()));
            return modelAndView;

        }

    }


    /**
     * 小说信息编辑页
     */
    @GetMapping("/infoManage/{novelId}")
    public ModelAndView novelInfoManage(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/novelInfoManagePage");
        NovelInfo info = novelService.findNovelInfo(novelId);
        modelAndView.addObject("info", info);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
        return  modelAndView;
    }


    /**
     * 进入小说目录编辑管理页面
     */
    @GetMapping("/novelIndexManage/{novelId}")
    public ModelAndView novelIndexManage(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        //页面处理加载
        modelAndView.setViewName("/creator/novelIndexManagePage");
        NovelIndex novelIndex = novelService.findNovelIndex(novelId);
        modelAndView.addObject("novelIndexInfo", novelIndex);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }


    /**
     * 小说目录
     * 返回一个小说所有的章节以及相关卷名
     * 在小说没有章节与卷的情况下，返回一个空list对象
     */
    @GetMapping("/findNovelIndex/{novel_id}")
    public ModelAndView findNovelIndex(@PathVariable(name = "novel_id") Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/findNovelIndex");

        NovelIndex novelIndex = novelService.findNovelIndex(novelId);
        modelAndView.addObject("novelIndexInfo", novelIndex);

        modelAndView.addObject("status", 0);
        modelAndView.addObject("msg", "success!");

        return modelAndView;
    }



    /**
     * 跳转添加新章节页面
     **/
    @GetMapping(path = "/enterAddChapterPage/{novelId}/{volumeId}")
    public ModelAndView addNewChapter(@PathVariable("novelId") Integer novelId, @PathVariable("volumeId") Integer volumeId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/addNewChapterPage");
        modelAndView.addObject("novelId", novelId);
        modelAndView.addObject("volumeId", volumeId);
        return modelAndView;
    }


    /**
     * 获取章节内容
     **/
    @GetMapping(path = "/findChapterContent/{chapterId}")
    public ModelAndView findChapterContent(@PathVariable Integer chapterId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/creator/contentPage");

        ChapterInfo chapterInfo = novelService.findNovelChapter(chapterId);
        modelAndView.addObject("chapterInfo", chapterInfo);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!"));
        return modelAndView;
    }



    /**
     * 章节管理页面填充
     */
    @GetMapping("/chapterManage/{novelId}")
    public ModelAndView chapterManagePage(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        NovelIndex index = novelService.findNovelIndex(novelId);

        modelAndView.setViewName("/creator/chapterManagePage");

        modelAndView.addObject("index", index);

        return modelAndView;
    }




}
