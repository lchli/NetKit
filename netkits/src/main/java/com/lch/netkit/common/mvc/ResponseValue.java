package com.lch.netkit.common.mvc;

/**
 * Created by lichenghang on 2018/1/28.
 */

public class ResponseValue<DATA> {

    public DATA data;

    public MvcError err;

    public boolean hasError() {
        return err != null;
    }

    public String errMsg() {
        return err != null ? err.msg : "";
    }

    public void setErrMsg(String msg) {
        err = new MvcError(msg);
    }

    public void setErrMsg(int code,String msg) {
        err = new MvcError(code, msg);
    }

}
