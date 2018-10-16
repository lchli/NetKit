package com.lch.netkit.v2.parser;

/**
 * Created by lichenghang on 2017/12/3.
 */

public interface Parser<T> {

    T parse(String responseString) throws Throwable;
}
