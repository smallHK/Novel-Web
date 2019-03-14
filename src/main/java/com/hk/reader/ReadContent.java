package com.hk.reader;

import com.hk.Chapter;
import com.hk.data.ShamData;

/**
 * @Author: smallHK
 * @Date: 2019/3/11 11:54
 *
 * 查看小说相关信息
 */
public class ReadContent {

    private ShamData shamData;

    public Chapter readCapter() {

        return shamData.queryChapter();
    }



}
