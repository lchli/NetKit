package com.lch.netkit.v2.okinterceptor;

import androidx.annotation.NonNull;

import com.lch.netkit.v2.apirequest.ApiRequestParams;


public interface ApiInterceptor {

    @NonNull
    ApiRequestParams interceptApiRequestParams(@NonNull ApiRequestParams requestParams);

    String interceptResponse(String responseString);

}
