package com.lch.netkit.v2.okinterceptor;

import com.lch.netkit.v2.util.HttpRequestUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
@Deprecated
 class ApiInterceptor2 implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        Headers headers = request.headers();
        Set<String> headerNames = headers.names();
        Map<String, String> rawHeaderMap = new HashMap<>();
        if (!headerNames.isEmpty()) {
            for (String k : headerNames) {
                rawHeaderMap.put(k, headers.get(k));
            }
        }

        Map<String, String> rawParamMap = new HashMap<>();

        Set<String> queryParamNames = request.url().queryParameterNames();
        for (String k : queryParamNames) {
            rawParamMap.put(k, request.url().queryParameter(k));
        }

        RequestBody body = request.body();
        if (body instanceof FormBody) {
            FormBody formBody = (FormBody) body;
            int sz = formBody.size();
            for (int i = 0; i < sz; i++) {
                rawParamMap.put(formBody.name(i), formBody.value(i));
            }

        }

        Request.Builder requestBuilder = request.newBuilder();
        if (rawHeaderMap != null) {
            for (Map.Entry<String, String> entry : rawHeaderMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (rawParamMap != null) {

            if (body instanceof FormBody){
                FormBody.Builder bodyBd=new FormBody.Builder();
                for (Map.Entry<String, String> entry : rawParamMap.entrySet()) {
                    bodyBd.add(entry.getKey(),entry.getValue());
                }

                if(method.equalsIgnoreCase("POST")){
                    requestBuilder.post(bodyBd.build());
                }else if(method.equalsIgnoreCase("PUT")){
                    requestBuilder.put(bodyBd.build());
                }else if(method.equalsIgnoreCase("PATCH")){
                    requestBuilder.patch(bodyBd.build());
                }

            }else{
                String rawUrl = request.url().url().toString();
                int index=rawUrl.indexOf("?");
                if(index>=0){
                    rawUrl=rawUrl.substring(0,index);
                }

                rawUrl= HttpRequestUtil.addParamToUrl(rawUrl,rawParamMap);
                requestBuilder.url(rawUrl);
            }
        }

        return null;
    }
}
