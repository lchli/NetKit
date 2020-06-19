package com.lch.netkit.v2.parser;

import com.alibaba.fastjson.JSON;

public class ModelParser<T> implements Parser<T> {
    private Class<T> clazz;

    public ModelParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(String responseString) {
        return JSON.parseObject(responseString, clazz);
    }
}
