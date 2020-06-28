package com.lch.netkit.v2.okinterceptor;

import androidx.annotation.NonNull;

import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.filerequest.DownloadFileParams;
import com.lch.netkit.v2.filerequest.UploadFileParams;


public interface ApiInterceptor {

    @NonNull
    ApiRequestParams interceptApiRequestParams(@NonNull ApiRequestParams requestParams);

    @NonNull
    UploadFileParams interceptUploadFileParams(@NonNull UploadFileParams requestParams);

    @NonNull
    DownloadFileParams interceptDownloadFileParams(@NonNull DownloadFileParams requestParams);

    String interceptResponse(String responseString);

}
