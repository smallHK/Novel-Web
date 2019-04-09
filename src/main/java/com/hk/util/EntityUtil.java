package com.hk.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;

/**
 * @author smallHK
 * 2019/4/9 11:14
 */
public class EntityUtil {

    public static <T> JSONObject objectToJsonObject(T target, Class<T> c) {

        JSONObject jsonObject = new JSONObject();
        Field[] fields = c.getDeclaredFields();
        for(Field field: fields) {

        }

        return jsonObject;
    }
}
