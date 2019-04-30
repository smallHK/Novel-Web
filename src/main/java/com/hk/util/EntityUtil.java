package com.hk.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hk.entity.Novel;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author smallHK
 * 2019/4/9 11:14
 */
public class EntityUtil {

    /**
     * 利用反射将对象转化为json对象
     * 假设被转化的对象只有基本类型字段
     *
     * @param target 被转化对象
     * @param c      被转化对象类型
     */
    public static <T> JSONObject objectToJsonObject(T target, Class<T> c) {

        JSONObject jsonObject = new JSONObject();
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                jsonObject.put(field.getName(), field.get(target));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }


    /**
     * 将entity列表转化为json数组，并且每个entity都转为json对象
     *
     * @param entityList entity列表
     * @param <T>        entity类型
     * @return json数组
     */
    public static <T> JSONArray entityListToJSONArray(List<T> entityList, Class<T> entityClass) {
        JSONArray entities = new JSONArray();
        for (T entity : entityList) {
            JSONObject entityObject = EntityUtil.objectToJsonObject(entity, entityClass);
            entities.add(entityObject);
        }
        return entities;
    }

    public static void main(String[] args) {

        Novel novel = new Novel();
        novel.setId(1);
        novel.setStatus(1);
        novel.setNovelName("aaaa");
        novel.setBriefIntro("sdfad");
        novel.setAuthorId(12);

        System.out.println(objectToJsonObject(novel, Novel.class).toJSONString());
    }
}
