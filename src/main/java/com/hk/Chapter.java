package com.hk;

import java.util.List;

/**
 * @Author: smallHK
 * @Date: 2019/3/11 11:55
 */
public class Chapter {

    //章节内容
    private List<String> content;

    //章节标题
    private String title;


    private String volume;


    public void setContent(List<String> content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
