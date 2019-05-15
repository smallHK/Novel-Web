package com.hk.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.hk.constant.SessionProperty;
import com.hk.entity.Novel;
import com.hk.po.NovelInfo;
import com.hk.service.EditorService;
import com.hk.service.NovelService;
import com.hk.util.ResultUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author smallHK
 * 2019/5/13 21:47
 */
@RestController
@RequestMapping("/editor")
public class EditorRestController {

    private EditorService editorService;

    private NovelService novelService;

    public EditorRestController(EditorService editorService, NovelService novelService) {
        this.editorService = editorService;
        this.novelService = novelService;
    }

    /**
     * 判断小说是否可以推荐
     * 可以推荐返回true
     *
     * 可以推荐：不是正在推荐不是正在审核
     * 处于推荐状态，返回true
     */
    @RequestMapping("/judgeRecommendNovel/{novelId}")
    public JSONObject judgeRecommendNovel(@PathVariable Integer novelId) {
        boolean flag = editorService.judgeRecommendCapacity(novelId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("flag", flag);
    }

    /**
     * 查看登陆编辑审核管理的书籍
     */
    @GetMapping("/findAllManagedNovelInfo")
    public @ResponseBody JSONObject findAllPublishedNovelByEditor(HttpSession session) {
        Integer editorId = (Integer) session.getAttribute(SessionProperty.EDITOR_LOGIN_EDITOR_ID);
        List<NovelInfo> novelList = novelService.listAllNovelByEditorId(editorId);
        return ResultUtil.success("success!").toJSONObject()
                .fluentPut("infos", novelList);
    }


}
