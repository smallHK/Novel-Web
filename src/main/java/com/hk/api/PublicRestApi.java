package com.hk.api;

import com.alibaba.fastjson.JSONObject;
import com.hk.po.NovelInfo;
import com.hk.service.PublicApiService;
import com.hk.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author smallHK
 * 2019/5/17 16:18
 */
@RestController
@RequestMapping("/api/public")
public class PublicRestApi {

    private PublicApiService publicApiService;

    public PublicRestApi(PublicApiService publicApiService) {
        this.publicApiService = publicApiService;

    }

    /**
     * 通过书名获取开放小说信息
     *
     */
    @RequestMapping("/gainNovelInfo/{novelName}")
    public JSONObject gainNovelInfo(String novelName) {
        NovelInfo info = publicApiService.gainNovelInfo(novelName);
        if(Objects.nonNull(info)) {
            return ResultUtil.success("success!").toJSONObject().fluentPut("info", info);
        }else {
            return ResultUtil.failure("没有该小说").toJSONObject().fluentPut("sign", 1);
        }
    }
}
