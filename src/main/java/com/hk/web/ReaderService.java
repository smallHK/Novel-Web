package com.hk.web;

import com.hk.repository.ReaderRepo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author smallHK
 * 2019/4/3 22:07
 */
@Controller
@RequestMapping("/reader")
public class ReaderService {

    private ReaderRepo readerRepo;

    public ReaderService(ReaderRepo readerRepo) {
        this.readerRepo = readerRepo;
    }


    /**
     * 注册读者用户
     */
    @PostMapping("/registerNewReader")
    public ModelAndView registerNewReader() {

        return null;
    }

    /**
     * 读者登陆
      */
    @GetMapping("/login")
    public ModelAndView readerLogin() {

        return null;
    }

    /**
     * 发表小说评论
     */
    @PostMapping("/publicNovelComment")
    public ModelAndView publishNovelComment() {

        return null;
    }

}
