package com.hk.web;

import com.hk.entity.NovelComment;
import com.hk.entity.Reader;
import com.hk.repository.NovelCommentRepo;
import com.hk.repository.ReaderRepo;
import com.hk.util.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 读者服务bean
 *
 * @author smallHK
 * 2019/4/3 22:07
 */
@Controller
@RequestMapping("/reader")
public class ReaderService {

    private ReaderRepo readerRepo;

    private NovelCommentRepo novelCommentRepo;

    public ReaderService(ReaderRepo readerRepo, NovelCommentRepo novelCommentRepo) {
        this.readerRepo = readerRepo;
        this.novelCommentRepo = novelCommentRepo;
    }


    /**
     * 注册读者用户
     */
    @PostMapping("/registerNewReader")
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
    @PostMapping("/publicNovelComment/{novelId}")
    public ModelAndView publishNovelComment(@RequestParam Map<String, String> params, HttpSession session) {


        ModelAndView modelAndView = new ModelAndView();

        Integer readerId = (Integer) session.getAttribute("reader_id");
        Integer novelId = Integer.valueOf(params.get("novelId"));
        String content = params.get("content");

        NovelComment novelComment = new NovelComment();
        novelComment.setContent(content);
        novelComment.setReaderId(readerId);
        novelComment.setNovelId(novelId);

        try {
            Integer number = novelCommentRepo.countAllByNovelId(novelId);
            novelComment.setOrderNum(number + 1);

            novelCommentRepo.save(novelComment);

            modelAndView.setViewName("redirect:/visitor/novelInfo/" + novelId);
            modelAndView.addObject("resultInfo", ResultUtil.success("评论成功！").toJSONObject());
            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("/result");
            modelAndView.addObject("resultInfo", ResultUtil.failure("评论失败！").toJSONObject());
            return modelAndView;

        }

    }

}
