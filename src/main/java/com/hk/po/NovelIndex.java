package com.hk.po;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * 表示书籍的目录
 * @author smallHK
 * 2019/4/28 19:18
 */
@Getter
@Setter
public class NovelIndex {

    private Integer novelId;

    private String novelName;

    private List<VolumeInfo> volumeInfoList;
}
