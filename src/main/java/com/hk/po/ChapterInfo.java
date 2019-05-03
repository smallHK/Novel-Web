package com.hk.po;

import com.hk.entity.Paragraph;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 页面涉及的章节信息
 * @author smallHK
 * 2019/4/30 23:15
 */
@Getter
@Setter
public class ChapterInfo {

    //章节信息在数据库中的标识（未来可能被废除）
    private Integer id;

    //章节标题
    private String title;

    //章节字数
    private Integer wordCount;

    //点击数
    private Integer clickCount;

    //章节内容
    private List<Paragraph> paragraphList;

    //小说Id（未来可能被废除）
    private Integer novelId;

    //小说名
    private String novelTitle;

    //卷Id（未来可能被废除）
    private Integer volumeId;

    //卷名
    private String volumeTitle;

    //发布状态
    private Integer status;


    //下一章跨章翻阅
    //是否存在下一章
    private Boolean isNext;

    //下一章Id
    private Integer nextId;

    //是否存在上一章
    private Boolean isPreview;

    //上一章Id
    private Integer previewId;


}
