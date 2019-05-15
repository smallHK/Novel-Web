package com.hk.po;

import com.hk.entity.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 前台页面需要的小说数据
 *
 * @author smallHK
 * 2019/4/29 11:37
 */
@Getter
@Setter
public class NovelInfo {

    private Integer id;

    private String novelName;

    private String briefIntro;

    private String coverImg;

    private Integer authorId;

    private Integer status;

    private Integer wordCount;

    //作者笔名
    private String penName;

    private List<Tag> tagList;

}
