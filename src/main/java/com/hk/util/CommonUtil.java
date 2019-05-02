package com.hk.util;

/**
 * smallHK
 * 2019/4/3 11:57
 */
public class CommonUtil {

    /**
     * 清楚多余空格
     * 清楚多余空行
     */
    public static String textProcessing(String origin) {
        String noBlack = origin.replaceAll("\\p{Blank}{3,}", "");
        return noBlack.replaceAll("(?m)^\\s*$" + System.lineSeparator(), "");
    }
}
