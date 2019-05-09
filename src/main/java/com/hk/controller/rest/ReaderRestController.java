package com.hk.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.hk.po.NovelInfo;
import com.hk.service.NovelService;
import com.hk.util.ResultUtil;
import com.hk.util.SessionProperty;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

/**
 * @author smallHK
 * 2019/5/9 17:41
 */
@RestController
@RequestMapping("/reader")
public class ReaderRestController {


    private NovelService novelService;

    public ReaderRestController(NovelService novelService) {
        this.novelService = novelService;
    }

    /**
     * 加入收藏夹
     */
    @GetMapping("/addFavorite/{novelId}")
    public @ResponseBody
    JSONObject addFavoriteByRest(@PathVariable Integer novelId, HttpSession session) {
        Integer readerId = (Integer) session.getAttribute(SessionProperty.READER_LOGIN_READER_ID);
        novelService.addFavoriteRela(novelId, readerId);
        return ResultUtil.success("success!").toJSONObject();
    }

    /**
     * 移除收藏夹
     */
    @GetMapping("/removeFavorite/{novelId}")
    public @ResponseBody
    JSONObject removeFavoriteByRest(@PathVariable Integer novelId, HttpSession session) {
        Integer readerId = (Integer)session.getAttribute(SessionProperty.READER_LOGIN_READER_ID);
        novelService.removeFavoriteRela(novelId, readerId);
        return ResultUtil.success("success!").toJSONObject();
    }

    /**
     * 获取收藏夹
     */
    @GetMapping("/listFavorite/{pageNum}/{pageSize}")
    public @ResponseBody
    JSONObject listFavoriteByRest(HttpSession session, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        Integer readerId = (Integer)session.getAttribute(SessionProperty.READER_LOGIN_READER_ID);
        Integer offset = (pageNum - 1) * pageSize;
        List<NovelInfo> infos = novelService.listFavorite(readerId, offset, pageSize);
        return ResultUtil.success("success!").toJSONObject().fluentPut("novelInfos", infos);
    }

    /**
     * 判断是否已经登陆
     * 未登录返回false
     * 登陆返回true
     */
    @GetMapping("/judgementLoginStatus")
    public @ResponseBody
    JSONObject judgeLoginStatus(HttpSession session) {
        if(Objects.isNull(session.getAttribute(SessionProperty.READER_LOGIN_READER_NAME))){
            return ResultUtil.success("success!").toJSONObject().fluentPut("flag", false);
        }else {
            return ResultUtil.success("success!").toJSONObject().fluentPut("flag", true);
        }

    }

    /**
     * 判断是否已经被收藏
     * 已被收藏，flag返回true
     */
    @GetMapping("/judgeFavoriteStatus/{novelId}")
    public @ResponseBody
    JSONObject judgeFavoriteStatus(@PathVariable Integer novelId, HttpSession session) {
        Integer readerId = (Integer) session.getAttribute(SessionProperty.READER_LOGIN_READER_ID);
        boolean flag = novelService.judgeFavoriteStatus(novelId, readerId);
        return ResultUtil.success("success!").toJSONObject().fluentPut("flag", flag);

    }
}
