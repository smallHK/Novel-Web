package com.hk.util;

/**
 * 计算余弦相似性
 * @author smallHK
 * 2019/5/7 17:06
 */
public class CosineSimilarity {

    public static double calculate(int[] v1, int[] v2) {

        if(v1.length != v2.length) throw new RuntimeException("计算余弦值相似度的向量应该保证长度一致！");

        int pointProduct = 0;
        for(int i = 0; i < v1.length; i++) {
            pointProduct += v1[i] * v2[i];
        }

        double vl1 = vectorLen(v1);
        double vl2 = vectorLen(v2);

        return pointProduct / (vl1*vl2);
    }


    private static double vectorLen(int[] v1) {
        int length = 0;
        for (int e : v1) {
            length += e * e;
        }
        return Math.sqrt(length);
    }

}
