package com.hk.constant;

/**
 * @author smallHK
 * 2019/5/9 10:46
 */
public class EventStatus {

    //章节更新事件提交
    public static final Integer CHAPTER_UPDATE_SUBMITTED = 0;

    //章节更新事件处理
    public static final Integer CHAPTER_UPDATE_PASSED = 1;


    //编辑小说推荐时间状态，已提交
    public static final Integer EDITOR_RECOMMEND_SUBMITTED = 0;

    //编辑小说推荐时间状态，同意推荐
    public static final Integer EDITOR_RECOMMEND_PASSED = 1;

    //编辑小说推荐事件状态，拒绝推荐
    public static final Integer EDITOR_RECOMMEND_FAILURE = 2;

}
