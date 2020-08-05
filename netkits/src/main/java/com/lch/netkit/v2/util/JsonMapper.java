package com.lch.netkit.v2.util;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class JsonMapper {

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return JSON.parseObject(json, classOfT);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> fromJsonList(String json, Class<T> classOfT) {
        try {
            return JSON.parseArray(json, classOfT);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String tojson(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
