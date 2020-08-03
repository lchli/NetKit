package com.lch.netkit.v2.util;

import java.util.HashMap;
import java.util.Map;

public class ResultDto<DATA> {
    private int code;
    private String msg;
    private DATA data;
    private boolean hasError = false;
    private final Map<String, Object> extras = new HashMap<>(1);

    public ResultDto(int code, String msg, DATA data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultDto(DATA data) {
        this.data = data;
    }

    public ResultDto() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg == null ? "" : msg;
    }

    public ResultDto<DATA> setMsg(String msg) {
        this.msg = msg;
        hasError = true;
        return this;
    }

    public DATA getData() {
        return data;
    }

    public ResultDto<DATA> setData(DATA data) {
        this.data = data;
        return this;
    }

    public boolean hasError() {
        return hasError;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }
}