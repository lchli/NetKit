package com.lch.netkit.common.mvc;

/**
 * Created by lichenghang on 2018/1/28.
 */

public class ResponseValue<DATA> {

    public int code;
    private String errorMsg;
    public DATA data;

    private boolean hasError = false;

    public boolean hasError() {
        return hasError;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        hasError = true;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
