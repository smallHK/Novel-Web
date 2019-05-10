package com.hk.controller;

import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Favorite;
import com.hk.entity.NovelComment;
import com.hk.entity.Reader;
import com.hk.po.NovelCommentList;
import com.hk.repository.NovelCommentRepo;
import com.hk.repository.ReaderRepo;
import com.hk.service.NovelService;
import com.hk.service.ReaderService;
import com.hk.util.ResultUtil;
import com.hk.util.SessionProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 读者服务控制器
 *
 * @author smallHK
 * 2019/4/3 22:07
 */
@Controller
@RequestMapping("/reader")
public class ReaderController {


    private NovelService novelService;

    private ReaderService readerService;

    public ReaderController(ReaderService readerService, NovelService novelService) {
        this.readerService = readerService;
        this.novelService = novelService;
    }


    /**
     * 注册读者用户
     */
    @PostMapping("/register")
    public ModelAndView registerNewReader(@RequestParam Map<String, String> params) {
        ModelAndView modelAndView = new ModelAndView();
        readerService.register(params.get("username"), params.get("password"));
        modelAndView.setViewName("/reader/loginPage");
        modelAndView.addObject("resultInfo", ResultUtil.success("用户注册成功！").toJSONObject());
        return modelAndView;

    }

    /**
     * 读者登陆
     */
    @PostMapping("/login")
    public ModelAndView readerLogin(@RequestParam Map<String, String> params, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();

        String username = params.get("username");
        String password = params.get("password");

        if (Objects.nonNull(session.getAttribute(SessionProperty.READER_LOGIN_READER_NAME))) {
            modelAndView.setViewName("redirect:/reader/enterReaderCenter");
            modelAndView.addObject("resultInfo", ResultUtil.success("用户已经登陆！").toJSONObject());
            return modelAndView;
        }

        Reader reader = readerService.readerLogin(username, password);
        if (Objects.nonNull(reader)) {
            session.setAttribute(SessionProperty.READER_LOGIN_READER_NAME, username);
            session.setAttribute(SessionProperty.READER_LOGIN_READER_ID, reader.getId());
            modelAndView.setViewName("redirect:/reader/enterReaderCenter");
            modelAndView.addObject("resultInfo", ResultUtil.success("用户成功登陆！").toJSONObject());
            return modelAndView;

        }

        modelAndView.setViewName("redirect:/reader/loginPage");
        modelAndView.addObject("resultInfo", ResultUtil.failure("密码错误！").toJSONObject());
        return modelAndView;

    }


    /**
     * 用户登陆
     */
    @PostMapping("/loginByRest")
    public @ResponseBody
    JSONObject loginByRest(@RequestParam Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        Reader reader = readerService.readerLogin(username, password);

        if (Objects.nonNull(reader)) {
            session.setAttribute(SessionProperty.READER_LOGIN_READER_NAME, username);
            session.setAttribute(SessionProperty.READER_LOGIN_READER_ID, reader.getId());
            return ResultUtil.success("登陆成功！").toJSONObject().fluentPut("flag", 0);
        }

        return ResultUtil.success("密码错误！").toJSONObject().fluentPut("flag", 1);
    }

    /**
     * 发表小说评论
     */
    @PostMapping("/publicNovelComment")
    public ModelAndView publishNovelComment(@RequestParam Map<String, String> params, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer readerId = (Integer) session.getAttribute(SessionProperty.READER_LOGIN_READER_ID);
        String content = params.get("content");
        Integer novelId = Integer.valueOf(params.get("novelId"));
        novelService.publishNovelComment(readerId, novelId, content);
        modelAndView.setViewName("redirect:/visitor/novelInfo/" + novelId);
        modelAndView.addObject("resultInfo", ResultUtil.success("评论成功！").toJSONObject());
        return modelAndView;
    }

    /**
     * 进入读者中心
     */
    @GetMapping("/enterReaderCenter")
    public ModelAndView enterReaderCenter(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer readerId = (Integer)session.getAttribute(SessionProperty.READER_LOGIN_READER_ID);
        List<NovelCommentList> list = novelService.findAllNovelCommentList(readerId);
        modelAndView.setViewName("/reader/readerCenter");
        modelAndView.addObject("novelCommentList", list);
        modelAndView.addObject("resultInfo", ResultUtil.success("success!").toJSONObject());
        return modelAndView;
    }


}
