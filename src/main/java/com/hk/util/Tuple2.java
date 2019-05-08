package com.hk.util;

import lombok.Getter;
import lombok.Setter;

/**
 * @author smallHK
 * 2019/5/8 9:03
 */
@Getter
@Setter
public class Tuple2 <T, R>{

    private T first;

    private R second;

    public Tuple2(T first, R second) {
        this.first = first;
        this.second = second;
    }
}
