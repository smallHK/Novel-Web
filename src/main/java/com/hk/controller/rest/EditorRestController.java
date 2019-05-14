package com.hk.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.hk.service.EditorService;
import com.hk.util.ResultUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author smallHK
 * 2019/5/13 21:47
 */
@RestController
@RequestMapping("/editor")
public class EditorRestController {

    private EditorService editorService;

    public EditorRestController(EditorService editorService) {
        this.editorService = editorService;
    }

    /**
     * 判断小说是否为封面已推荐状态
     * 处于推荐状态，返回true
     */
    @RequestMapping("/judgeRecommendNovel/{novelId}")
    public JSONObject judgeRecommendNovel(@PathVariable Integer novelId) {
        boolean flag = editorService.judgeNovelRecommendStatus(novelId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("flag", flag);
    }

}
