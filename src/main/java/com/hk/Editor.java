package com.hk;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RestController;

/**
 * smallHK
 * 2019/3/19 21:32
 * 编辑主要使用的功能
 */
@RestController("/read")
public class Editor {


    /**
     * 获得被自己管理的图书列表???
     * 没有分页
     *
     * @return
     */
    public JSONObject listManagedNovel(String editorName, String editorId) {

        JSONObject result = new JSONObject();

        JSONArray novelList = new JSONArray();

        return result.fluentPut("status", 1)
                .fluentPut("data", novelList);
    }

    /**
     * 列出可以选择图书
     *
     * @return
     */
    public JSONObject listSelectingNovel() {

        return null;
    }


    /**
     * 禁止指定小说
     *
     * @return
     */
    public JSONObject prohibitNovel() {
        return null;
    }

    /**
     * 同意审核
     */
    public JSONObject checkNovel() {

        return null;
    }

    /**
     * 查找指定书籍
     * 根据小说名称查找
     */
}
