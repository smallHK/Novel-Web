package com.hk.po;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * 小说评论信息
 * @author smallHK
 * 2019/5/2 22:12
 */
@Getter
@Setter
public class NovelCommentInfo {

    private Integer id;

    private List<String> content;

    private Integer orderNum;

    private String commentTimeStr;

    private Instant commentTime; //评论时间



    private String username; //评论人

    private Integer readerId;

    private Integer novelId;


}
