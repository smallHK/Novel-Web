package com.hk.po;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * 事件
 *
 * @author smallHK
 * 2019/5/5 22:12
 */
@Setter
@Getter
public class EditorWorkEvent {

    //0表示卷开放事件
    //1表示章开放事件
    private Integer type;

    private Integer id;

    private Integer editorId;

    private Integer authorId;

    private Integer novelId;

    private String novelTitle;

    private Integer volumeId;

    private String volumeTitle;

    private Integer chapterId;

    private String chapterTitle;

    private Instant appearTime;

    private String appearTimeStr;






}
