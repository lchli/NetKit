package com.lch.netkit.v2.common;


import androidx.annotation.NonNull;

import com.lch.netkit.v2.util.ShareConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用参数，比如设置header，url，普通参数等。
 * Created by bbt-team on 2017/8/1.
 */

public abstract class CommonParams<T extends CommonParams> {

    private final Map<String, String> mParams = new HashMap<>();
    private final Map<String, String> mHeaders = new HashMap<>();
    private String url;

    protected abstract T thisObject();

    public T addParam(String key, String value) {
        if (key != null && value != null) {
            mParams.put(key, value);
        }
        return thisObject();
    }

    public T addParams(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            mParams.putAll(params);
        }
        return thisObject();
    }

    public T addHeader(String key, String value) {
        if (key != null && value != null) {
            mHeaders.put(key, value);
        }
        return thisObject();
    }

    public T addHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            mHeaders.putAll(headers);
        }
        return thisObject();
    }

    public T setUrl(String url) {
        this.url = url;
        return thisObject();
    }

    public T setGzip(boolean gzip) {
        mHeaders.put(ShareConstants.HTTP_HEADER_GZIP_POLICY, gzip ? ShareConstants.HTTP_HEADER_GZIP_YES : ShareConstants.HTTP_HEADER_GZIP_NO);
        return thisObject();
    }

    @NonNull
    public Map<String, String> params() {
        return mParams;
    }


    @NonNull
    public Map<String, String> headers() {
        return mHeaders;
    }

    public String getUrl() {
        return url == null ? "" : url;//avoid okhttp NPE.
    }


}
