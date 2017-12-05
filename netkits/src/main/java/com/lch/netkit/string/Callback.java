package com.lch.netkit.string;

/**
 * Created by lichenghang on 2017/12/3.
 */

public interface Callback<T> {

    void onSuccess(T parsedResult);

    void onFail(String msg);
}
