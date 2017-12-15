package com.lch.netkit.common.tool;

import com.alibaba.fastjson.JSON;

public final class JsonHelper {

    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return JSON.parseObject(text, clazz);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toJSONString(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
