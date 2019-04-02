package com.hk.vo;

import com.hk.entity.Chapter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * smallHK
 * 2019/3/29 20:51
 *
 *
 */
@Getter
@Setter
public class VolumeInfo {

    private String volumeTitle;

    private Integer orderNum;

    private List<Chapter> chapterList;

}
