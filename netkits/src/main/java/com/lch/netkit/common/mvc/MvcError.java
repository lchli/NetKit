package com.lch.netkit.common.mvc;

/**
 * Created by Administrator on 2017/9/30.
 */

public class MvcError {

    public int code;
    public String msg;

    public MvcError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MvcError(String msg) {
        this.msg = msg;
    }
}
