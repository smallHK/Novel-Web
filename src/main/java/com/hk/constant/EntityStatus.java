package com.hk.constant;

/**
 * @author smallHK
 * 2019/4/7 20:31
 */
public class EntityStatus {

    public static final Integer PROFILE_UNREAD = 0;

    public static final Integer PROFILE_PASSED = 1;


    //小说未开放状态
    public static final Integer NOVLE_CREATED = 0;

    //小说审核中状态
    public static final Integer NOVEL_CHECKING = 1;

    //小说开放状态
    public static final Integer NOVEL_PASSED = 2;


    //小说连载中状态
    public static final Integer NOVEL_UNCOMPLETED = 0;



    //章节发布状态，未开放
    public static final Integer CHAPTER_CREATED = 0;

    //审核中
    public static final Integer CHAPTER_CHECKING = 1;

    //开放
    public static final Integer CHAPTER_PASSED = 2;



    //章节开放事件提交
    public static final Integer CHAPTER_PUBLISH_EVENT_SUBMITTED = 0;

    //章节开放事件处理
    public static final Integer CHAPTER_PUBLISH_EVENT_PASSED = 1;




    //卷发布状态，未开放
    public static final Integer VOLUME_CREATED = 0;

    //审核中
    public static final Integer VOLUME_CHECKING = 1;

    //开放
    public static final Integer VOLUME_PASSED = 2;


    //卷开放事件提交
    public static final Integer VOLUME_PUBLISH_EVENT_SUBMITTED = 0;

    //卷开放事件处理
    public static final Integer VOLUME_PUBLISH_EVENT_PASSED = 1;





}
