package com.hk.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author smallHK
 * 2019/5/8 9:36
 */
public class CollectionUtil {

    public static <T> List<T> iterableToList(Iterable<T> iterable) {

        List<T> list = new ArrayList<>();

        for(T t : iterable) {
            list.add(t);
        }

        return list;
    }
}
