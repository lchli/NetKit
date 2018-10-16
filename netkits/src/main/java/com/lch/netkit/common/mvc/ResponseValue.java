package com.lch.netkit.common.mvc;

/**
 * Created by lichenghang on 2018/1/28.
 */

public class ResponseValue<DATA> {

    public int code;
    public String errorMsg;
    public DATA data;

    public boolean hasError() {
        return this.errorMsg != null;
    }

}
