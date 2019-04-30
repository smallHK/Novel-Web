package com.hk.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Creator;
import com.hk.entity.Novel;
import com.hk.entity.NovelComment;
import com.hk.repository.CreatorRepository;
import com.hk.repository.NovelCommentRepo;
import com.hk.repository.NovelRepository;
import com.hk.util.EntityStatus;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

/**
 * 无帐户游客调用的接口
 *
 * @author smallHK
 * 2019/4/15 9:01
 */
@Controller
@RequestMapping("/visitor")
public class VisitorService {


    private NovelRepository novelRepository;

    private NovelCommentRepo novelCommentRepo;

    private CreatorRepository creatorRepository;

    public VisitorService(NovelRepository novelRepository, NovelCommentRepo novelCommentRepo, CreatorRepository creatorRepository) {
        this.novelRepository = novelRepository;
        this.novelCommentRepo = novelCommentRepo;
        this.creatorRepository = creatorRepository;
    }

    /**
     * 获取所有公开小说
     * <p>
     * 小说状态为通过审核
     */
    @GetMapping("/listAllPublishedNovel")
    public @ResponseBody
    JSONObject listAllPublishedNovel() {
        try {
            List<Novel> novelList = novelRepository.findAllByStatus(EntityStatus.NOVEL_PASSED);
            return ResultUtil.success("success!").toJSONObject().fluentPut("novelList", novelList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.success("failure!").toJSONObject();

        }
    }

    /**
     * 获取小说的目录与卷消息
     */
    @GetMapping("/findSpecialPublishedNovelIndex/{novelId}")
    public ModelAndView getPublishedNovelIndex(@PathVariable Integer novelId) {

        ModelAndView modelAndView = new ModelAndView();

        return modelAndView;

    }

    /**
     * 获取小说数据
     * <p>
     * 小说状态为通过审核
     *
     * 显示制定小说的页面
     */
    @GetMapping("/novelInfo/{novelId}")
    public ModelAndView get(@PathVariable Integer novelId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Novel novel = novelRepository.findNovelById(novelId);
            Optional<Creator> creator = creatorRepository.findById(novel.getAuthorId());
            if (creator.isEmpty()) throw new RuntimeException("No Creator!");
            modelAndView.setViewName("/visitor/novelInfo");
            modelAndView.addObject("novel", novel);
            modelAndView.addObject("authorName", creator.get().getPenName());
            modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("failure!").toJSONObject());
            return modelAndView;
        }
    }



    /**
     * 查看所有评论
     */
    @GetMapping("/listAllNovelComment")
    public @ResponseBody
    JSONObject listAllNovelComment(Integer novelId) {
        try {

            List<NovelComment> novelCommentList = novelCommentRepo.findAllByNovelId(novelId);

            JSONArray data = new JSONArray();
            for(NovelComment novelComment: novelCommentList) {
                JSONObject record = new JSONObject();
            }

            return ResultUtil.success("success!").toJSONObject().fluentPut("novelCommentList", novelCommentList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.success("failure!").toJSONObject();

        }
    }

}
