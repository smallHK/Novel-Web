package com.hk.po;

import com.hk.entity.Chapter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * smallHK
 * 2019/3/29 20:51
 * <p>
 * 传到前台显示的页面数据
 * <p>
 * 目录里的卷部分
 */
@Getter
@Setter
public class VolumeInfo {

    private Integer volumeId;

    private String volumeTitle;

    private Integer orderNum;

    private Integer status;

    private List<Chapter> chapterList;

    private Integer novelId;

}
