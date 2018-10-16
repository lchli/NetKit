package com.lch.netkit.v2.common;


import com.lch.netkit.v2.util.ShareConstants;

public class NetworkResponse<DATA> {

    public int httpCode= ShareConstants.HTTP_ERR_CODE_UNKNOWN;
    public String errorMsg;
    public DATA data;

    public boolean hasError() {
        return this.errorMsg != null;
    }

}
