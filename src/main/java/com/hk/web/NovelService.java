package com.hk.web;

import com.alibaba.fastjson.JSONObject;
import com.hk.ShamData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * smallHK
 * 2019/3/21 16:12
 */
@Controller
@RequestMapping(path = "/novel")
public class NovelService {


    /**
     * 添加章节
     *
     * @param chapter
     * @return
     */
    @PostMapping(path = "/edit")
    public @ResponseBody
    JSONObject addNewNovel(@RequestParam Map<String, String> chapter) {
        JSONObject result = new JSONObject();

        String content = chapter.get("content");
        String[] ps = content.split("\n");
        for (int i = 0; i < ps.length; i++) {
            System.out.println(i + ": " + ps[i]);
        }
        result.fluentPut("status", "0").fluentPut("msg", "success!");
        return result;
    }

    /**
     * 查看本章小说内容
     */
    @GetMapping(path = "/content")
    public String readChpaterContent(Model model) {
        ShamData shamData = new ShamData();
        model.addAttribute("content", shamData.getText());
        return "Content.html";
    }


}
