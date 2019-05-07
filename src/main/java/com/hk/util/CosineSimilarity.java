package com.hk.util;

/**
 * 计算余弦相似性
 * @author smallHK
 * 2019/5/7 17:06
 */
public class CosineSimilarity {

    public static double calculate(int[] v1, int[] v2) {

        if(v1.length != v2.length) throw new RuntimeException("计算余弦值相似度的向量应该");

        int pointProduct = 0;
        for(int i = 0; i < v1.length; i++) {
            pointProduct += v1[i] * v2[i];
        }


        for(int i = 0; i < v1.length; i++) {
         v1[i] * v1[i];
        }

        return 0;
    }


    public static double vectorLen(int[] v1) {

    }
}
