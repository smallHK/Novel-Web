package com.hk.controller;

import com.hk.entity.NovelComment;
import com.hk.entity.Reader;
import com.hk.repository.NovelCommentRepo;
import com.hk.repository.ReaderRepo;
import com.hk.service.NovelService;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * 读者服务控制器
 *
 * @author smallHK
 * 2019/4/3 22:07
 */
@Controller
@RequestMapping("/reader")
public class ReaderController {

    private ReaderRepo readerRepo;

    private NovelCommentRepo novelCommentRepo;

    private NovelService novelService;

    public ReaderController(ReaderRepo readerRepo, NovelCommentRepo novelCommentRepo, NovelService novelService) {
        this.readerRepo = readerRepo;
        this.novelCommentRepo = novelCommentRepo;
        this.novelService = novelService;
    }


    /**
     * 注册读者用户
     */
    @PostMapping("/register")
    public ModelAndView registerNewReader(@RequestParam Map<String, String> params) {
        ModelAndView modelAndView = new ModelAndView();
        String username = params.get("username");
        String password = params.get("password");
        Reader reader = new Reader();
        reader.setUsername(username);
        reader.setPassword(password);

        try {
            readerRepo.save(reader);
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.success("用户注册成功！").toJSONObject());
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("用户注册失败！").toJSONObject());
            return modelAndView;
        }
    }

    /**
     * 读者登陆
     */
    @PostMapping("/login")
    public ModelAndView readerLogin(@RequestParam Map<String, String> params, HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();

        String username = params.get("username");
        String password = params.get("password");

        try {
            Iterable<Reader> readers = readerRepo.findAll();
            for (Reader reader : readers) {
                if (reader.getUsername().equals(username) && reader.getPassword().equals(password)) {
                    session.setAttribute("reader_name", username);
                    session.setAttribute("reader_id", reader.getId());
                    modelAndView.setViewName("/reader/readerCenter");
                    modelAndView.addObject("resultInfo", ResultUtil.success("用户成功登陆！").toJSONObject());
                    return modelAndView;
                }
            }
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("没有此用户！").toJSONObject());
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("用户登陆失败！").toJSONObject());
            return modelAndView;
        }
    }

    /**
     * 发表小说评论
     */
    @PostMapping("/publicNovelComment")
    public ModelAndView publishNovelComment(@RequestParam Map<String, String> params, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Integer readerId = (Integer) session.getAttribute("reader_id");
        String content = params.get("content");
        Integer novelId = Integer.valueOf(params.get("novelId"));
        novelService.publishNovelComment(readerId, novelId, content);
        modelAndView.setViewName("redirect:/visitor/novelInfo/" + novelId);
        modelAndView.addObject("resultInfo", ResultUtil.success("评论成功！").toJSONObject());
        return modelAndView;
    }


}
