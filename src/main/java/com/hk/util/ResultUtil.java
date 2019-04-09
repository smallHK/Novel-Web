package com.hk.util;

import com.alibaba.fastjson.JSONObject;

/**
 * @author smallHK
 * 2019/4/4 11:18
 */
public class ResultUtil {

    public static final Integer SUCCESS_STATUS = 0;

    public static final Integer FAILURE_STATUS = 1;

    public static ResultInfo success(String msg) {

        return new ResultInfo(msg, SUCCESS_STATUS);

    }

    public static ResultInfo failure(String msg) {

        return new ResultInfo(msg, FAILURE_STATUS);
    }

    public static class ResultInfo {

        private String msg;

        private Integer status;

        ResultInfo(String msg, Integer status) {
            this.msg = msg;
            this.status = status;
        }

        public JSONObject toJSONObject() {
            return new JSONObject().fluentPut("msg", msg)
                    .fluentPut("status", status);
        }
    }
}
