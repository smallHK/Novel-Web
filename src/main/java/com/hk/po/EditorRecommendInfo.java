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

    private Integer novelId;

    private String novelName;

    private String authorName;

    private Integer wordCount;

    private Integer clickCount;

    private Integer favoriteCount;

    private String briefIntro;

    private String editorName;

    private String reason;

}
