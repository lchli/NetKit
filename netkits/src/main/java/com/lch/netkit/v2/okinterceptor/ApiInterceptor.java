package com.lch.netkit.v2.okinterceptor;


import android.support.annotation.NonNull;

import com.lch.netkit.v2.apirequest.ApiRequestParams;


public interface ApiInterceptor {

    /**
     * 对参数进行处理，比如添加公共参数等。
     *
     * @param requestParams
     * @return
     */
    @NonNull
    ApiRequestParams interceptApiRequestParams(@NonNull ApiRequestParams requestParams);

    /**
     * 对响应内容进行处理。
     *
     * @param responseString
     * @return
     */
    String interceptResponse(String responseString);

}
