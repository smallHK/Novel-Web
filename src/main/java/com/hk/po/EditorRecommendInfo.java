package com.hk.po;

import lombok.Getter;
import lombok.Setter;

/**
 * @author smallHK
 * 2019/5/13 22:30
 */
@Getter
@Setter
public class EditorRecommendInfo {

    private Integer id;//可能会废除

    private Integer novelId;

    private String novelName;

    private String coverImg;

    private String authorName;

    private Integer wordCount;

    private Integer clickCount;

    private Integer favoriteCount;

    private String briefIntro;

    private String editorName;

    private String reason;

}
