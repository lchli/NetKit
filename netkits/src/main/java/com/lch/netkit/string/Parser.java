package com.lch.netkit.string;

/**
 * Created by lichenghang on 2017/12/3.
 */

public interface Parser<T> {

    T parse(String responseString);
}
