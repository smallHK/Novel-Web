package com.hk.po;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author smallHK
 * 2019/5/10 17:11
 */
@Getter
@Setter
public class NovelCommentList {

    private Integer novelId;

    private String novelName;

    private List<NovelCommentInfo> commentInfoList;

}
