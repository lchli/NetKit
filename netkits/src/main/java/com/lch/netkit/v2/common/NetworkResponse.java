package com.lch.netkit.v2.common;


import com.lch.netkit.v2.util.ShareConstants;

public class NetworkResponse<DATA> {

    public String httpCode = ShareConstants.HTTP_ERR_CODE_UNKNOWN;
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
