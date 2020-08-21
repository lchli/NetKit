package com.lch.netkit.v2.apirequest;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.common.Cancelable;
import com.lch.netkit.v2.common.NetKitException;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.parser.ModelParser;
import com.lch.netkit.v2.parser.Parser;
import com.lch.netkit.v2.util.HttpMethodType;
import com.lch.netkit.v2.util.ShareConstants;
import com.lch.netkit.v2.util.StreamUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.lch.netkit.v2.util.CallbackUtil.onError;
import static com.lch.netkit.v2.util.CallbackUtil.onSuccess;


public class ApiRequest {


    /**
     * 异步网络get请求。
     *
     * @param params   请求参数。
     * @param parser   响应内容的解析器。
     * @param callback 回调。
     * @param <T>
     * @return 可取消请求的对象。
     */
    @Nullable
    public <T> Cancelable asyncGet(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser, final RequestCallback<T> callback) {
        return asyncRequestImpl(params, parser, callback, HttpMethodType.GET);
    }

    @Nullable
    public <T> Cancelable asyncGet(@NonNull final ApiRequestParams params, @NonNull final Class<T> resultClass, final RequestCallback<T> callback) {
        return asyncRequestImpl(params, new ModelParser<>(resultClass), callback, HttpMethodType.GET);
    }


    /**
     * 异步网络post请求。
     *
     * @param params   请求参数。
     * @param parser   响应内容的解析器。
     * @param callback 回调。
     * @param <T>
     * @return 可取消请求的对象。
     */
    @Nullable
    public <T> Cancelable asyncPost(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser, final RequestCallback<T> callback) {
        return asyncRequestImpl(params, parser, callback, HttpMethodType.POST);
    }

    @Nullable
    public <T> Cancelable asyncPost(@NonNull final ApiRequestParams params, @NonNull final Class<T> resultClass, final RequestCallback<T> callback) {
        return asyncRequestImpl(params, new ModelParser<>(resultClass), callback, HttpMethodType.POST);
    }

    public <T> Cancelable asyncRequestImpl(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser, final RequestCallback<T> callback, final HttpMethodType methodType) {
        final Cancelable cancelable = new Cancelable();

        NetKit.Internal.runAsync(new Runnable() {

            @Override

            public void run() {
                Response response = null;
                String responseCode = ShareConstants.HTTP_ERR_CODE_UNKNOWN;

                try {

                    response = executeOkhttp(NetKit.Internal.interceptApiRequestParams(params), methodType, cancelable);
                    responseCode = response.code() + "";

                    if (!response.isSuccessful()) {
                        onError(responseCode, response.message(), callback);
                        return;
                    }

                    ResponseBody body = response.body();
                    if (body == null) {
                        onError(responseCode, "response body is null.", callback);
                        return;
                    }

                    final String bodystring = NetKit.Internal.interceptResponse(body.string());

                    onSuccess(responseCode, parser.parse(bodystring), callback);

                } catch (final Throwable e) {
                    e.printStackTrace();

                    if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                        responseCode = ((NetKitException) e).getHttpCode();
                    }

                    onError(responseCode, e.getMessage() + "", callback);

                } finally {
                    StreamUtils.closeStreams(response);
                }


            }

        });

        return cancelable;
    }


    /**
     * 同步网络get请求。
     *
     * @param params 请求参数。
     * @param parser 响应内容的解析器。默认解析器
     * @param <T>
     * @return 响应对象。
     */
    @NonNull
    public <T> NetworkResponse<T> syncGet(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser) {
        return syncRequestImpl(params, parser, HttpMethodType.GET);
    }

    @NonNull
    public <T> NetworkResponse<T> syncGet(@NonNull final ApiRequestParams params, @NonNull final Class<T> resultClass) {
        return syncRequestImpl(params, new ModelParser<>(resultClass), HttpMethodType.GET);
    }

    /**
     * 同步网络post请求。
     *
     * @param params 请求参数。
     * @param parser 响应内容的解析器。默认解析器
     * @param <T>
     * @return 响应对象。
     */
    @NonNull
    public <T> NetworkResponse<T> syncPost(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser) {
        return syncRequestImpl(params, parser, HttpMethodType.POST);
    }

    @NonNull
    public <T> NetworkResponse<T> syncPost(@NonNull final ApiRequestParams params, @NonNull final Class<T> resultClass) {
        return syncRequestImpl(params, new ModelParser<>(resultClass), HttpMethodType.POST);
    }

    public <T> NetworkResponse<T> syncRequestImpl(@NonNull ApiRequestParams params, @NonNull final Parser<T> parser, HttpMethodType methodType) {
        final NetworkResponse<T> responseValue = new NetworkResponse<>();
        Response response = null;

        try {
            response = executeOkhttp(NetKit.Internal.interceptApiRequestParams(params), methodType, null);
            responseValue.httpCode = response.code() + "";

            if (!response.isSuccessful()) {
                responseValue.setErrorMsg(response.message());
                return responseValue;
            }

            ResponseBody body = response.body();
            if (body == null) {
                responseValue.setErrorMsg("response body is null");
                return responseValue;
            }

            String bodystring = NetKit.Internal.interceptResponse(body.string());

            responseValue.data = parser.parse(bodystring);

            return responseValue;

        } catch (final Throwable e) {
            e.printStackTrace();

            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                responseValue.httpCode = ((NetKitException) e).getHttpCode();
            }

            responseValue.setErrorMsg(e.getMessage() + "");

            return responseValue;

        } finally {
            StreamUtils.closeStreams(response);
        }

    }

    private Response executeOkhttp(@NonNull final ApiRequestParams params, HttpMethodType methodType, @Nullable Cancelable cancelable) throws IOException {
        final Request.Builder requestBuilder = new Request.Builder();

        Map<String, String> headers = params.headers();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey() != null) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }

        }
        String url = params.getUrl();

        switch (methodType) {
            case POST:
                if (params.getRequestBody() != null) {
                    requestBuilder.url(url).post(params.getRequestBody());
                } else {
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    Map<String, String> paramIter = params.params();
                    for (Map.Entry<String, String> entry : paramIter.entrySet()) {
                        if (entry.getKey() != null) {
                            formBuilder.add(entry.getKey(), entry.getValue());
                        }

                    }
                    requestBuilder.url(url).post(formBuilder.build());
                }
                break;
            case GET:
                if (!url.contains("?")) {
                    url += "?";
                }
                if (!url.endsWith("?")) {
                    url += "&";
                }
                StringBuilder sb = new StringBuilder(url);
                Map<String, String> paramIter = params.params();
                for (Map.Entry<String, String> entry : paramIter.entrySet()) {
                    if (entry.getKey() != null) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                }
                url = sb.toString();

                if (url.endsWith("&")) {
                    url = url.substring(0, url.length() - 1);
                }

                requestBuilder.url(url)
                        .get();
                break;

            case PUT:
                requestBuilder.url(url).put(params.getRequestBody());
                break;
            case DELETE:
                if (params.getRequestBody() != null) {
                    requestBuilder.url(url).delete(params.getRequestBody());
                } else {
                    requestBuilder.url(url).delete();
                }
                break;
            case PATCH:
                requestBuilder.url(url).patch(params.getRequestBody());
                break;
            case HEAD:
                requestBuilder.url(url).head();
                break;

        }

        Call call = NetKit.client().newCall(requestBuilder.build());
        if (cancelable != null) {
            cancelable.setCall(call);
        }

        return call.execute();
    }

}