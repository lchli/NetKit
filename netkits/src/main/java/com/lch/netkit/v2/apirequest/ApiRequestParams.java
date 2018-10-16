package com.lch.netkit.v2.apirequest;


import com.lch.netkit.v2.common.CommonParams;

import okhttp3.RequestBody;

/**
 * Created by lichenghang.
 */

public class ApiRequestParams extends CommonParams<ApiRequestParams> {

    private RequestBody requestBody;


    public ApiRequestParams setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return thisObject();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    @Override
    protected ApiRequestParams thisObject() {
        return this;
    }


}
