package com.lch.netkit.v2.apirequest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.common.Cancelable;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.parser.Parser;
import com.lch.netkit.v2.util.ShareConstants;
import com.lch.netkit.v2.util.StreamUtils;

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
     * @param parser   响应内容的解析器。默认解析器{@see Parsers#JSON}，{@see Parsers#STRING}
     * @param callback 回调。
     * @param <T>
     * @return 可取消请求的对象。
     */
    @Nullable
    public <T> Cancelable asyncGet(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser, final RequestCallback<T> callback) {
        final Cancelable cancelable = new Cancelable();

        NetKit.Internal.runAsync(new Runnable() {

            @Override
            public void run() {
                Response response = null;

                try {
                    Request.Builder requestBuilder = new Request.Builder();

                    String url = params.getUrl();

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

                    Map<String, String> headers = params.headers();

                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        if (entry.getKey() != null) {
                            requestBuilder.addHeader(entry.getKey(), entry.getValue());
                        }

                    }

                    requestBuilder.url(url)
                            .get();

                    Call call = NetKit.client().newCall(requestBuilder.build());
                    cancelable.setCall(call);

                    response = call.execute();

                    if (!response.isSuccessful()) {
                        onError(response.code(), response.message(), callback);
                        return;
                    }


                    ResponseBody body = response.body();

                    if (body == null) {
                        onError(response.code(), "response body is null.", callback);
                        return;
                    }

                    onSuccess(response.code(), parser.parse(body.string()), callback);

                } catch (final Throwable e) {
                    e.printStackTrace();

                    int code = response != null ? response.code() : ShareConstants.HTTP_ERR_CODE_UNKNOWN;
                    onError(code, e.getMessage() + "", callback);

                } finally {
                    StreamUtils.closeStreams(response);
                }


            }

        });

        return cancelable;

    }


    /**
     * 异步网络post请求。
     *
     * @param params   请求参数。
     * @param parser   响应内容的解析器。默认解析器{@see com.babytree.baf.network.parser.Parsers#JSON}，{@see com.babytree.baf.network.parser.Parsers#STRING}
     * @param callback 回调。
     * @param <T>
     * @return 可取消请求的对象。
     */
    @Nullable
    public <T> Cancelable asyncPost(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser, final RequestCallback<T> callback) {
        final Cancelable cancelable = new Cancelable();

        NetKit.Internal.runAsync(new Runnable() {

            @Override

            public void run() {
                Response response = null;

                try {

                    Request.Builder requestBuilder = new Request.Builder();

                    FormBody.Builder formBuilder = new FormBody.Builder();


                    String url = params.getUrl();

                    Map<String, String> paramIter = params.params();

                    for (Map.Entry<String, String> entry : paramIter.entrySet()) {
                        if (entry.getKey() != null) {
                            formBuilder.add(entry.getKey(), entry.getValue());
                        }

                    }


                    Map<String, String> headers = params.headers();

                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        if (entry.getKey() != null) {
                            requestBuilder.addHeader(entry.getKey(), entry.getValue());
                        }

                    }


                    if (params.getRequestBody() != null) {

                        requestBuilder.url(url)

                                .post(params.getRequestBody());

                    } else {

                        requestBuilder.url(url)

                                .post(formBuilder.build());

                    }

                    Call call = NetKit.client().newCall(requestBuilder.build());
                    cancelable.setCall(call);

                    response = call.execute();

                    if (!response.isSuccessful()) {
                        onError(response.code(), response.message(), callback);
                        return;
                    }


                    ResponseBody body = response.body();

                    if (body == null) {
                        onError(response.code(), "response body is null.", callback);
                        return;
                    }

                    onSuccess(response.code(), parser.parse(body.string()), callback);

                } catch (final Throwable e) {
                    e.printStackTrace();

                    int code = response != null ? response.code() : ShareConstants.HTTP_ERR_CODE_UNKNOWN;
                    onError(code, e.getMessage() + "", callback);

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
     * @param parser 响应内容的解析器。默认解析器{@see com.babytree.baf.network.parser.Parsers#JSON}，{@see com.babytree.baf.network.parser.Parsers#STRING}
     * @param <T>
     * @return 响应对象。
     */
    @NonNull
    public <T> NetworkResponse<T> syncGet(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser) {

        NetworkResponse<T> responseValue = new NetworkResponse<>();
        Response response = null;

        try {
            Request.Builder requestBuilder = new Request.Builder();

            String url = params.getUrl();

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


            Map<String, String> headers = params.headers();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey() != null) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }

            }


            requestBuilder.url(url)

                    .get();


            response = NetKit.client().newCall(requestBuilder.build()).execute();

            if (!response.isSuccessful()) {
                responseValue.httpCode = response.code();
                responseValue.setErrorMsg(response.message());

                return responseValue;
            }


            ResponseBody body = response.body();

            if (body == null) {
                responseValue.httpCode = response.code();
                responseValue.setErrorMsg("response body is null");
                return responseValue;
            }


            responseValue.data = parser.parse(body.string());


            return responseValue;


        } catch (final Throwable e) {

            e.printStackTrace();

            if (response != null) {
                responseValue.httpCode = response.code();
            }
            responseValue.setErrorMsg(e.getMessage());

            return responseValue;

        } finally {
            StreamUtils.closeStreams(response);
        }


    }

    /**
     * 同步网络post请求。
     *
     * @param params 请求参数。
     * @param parser 响应内容的解析器。默认解析器{@see com.babytree.baf.network.parser.Parsers#JSON}，{@see com.babytree.baf.network.parser.Parsers#STRING}
     * @param <T>
     * @return 响应对象。
     */
    @NonNull
    public <T> NetworkResponse<T> syncPost(@NonNull final ApiRequestParams params, @NonNull final Parser<T> parser) {

        NetworkResponse<T> responseValue = new NetworkResponse<>();
        Response response = null;

        try {

            Request.Builder requestBuilder = new Request.Builder();

            FormBody.Builder formBuilder = new FormBody.Builder();


            String url = params.getUrl();

            Map<String, String> paramIter = params.params();

            for (Map.Entry<String, String> entry : paramIter.entrySet()) {
                if (entry.getKey() != null) {
                    formBuilder.add(entry.getKey(), entry.getValue());
                }

            }


            Map<String, String> headers = params.headers();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey() != null) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }

            }


            if (params.getRequestBody() != null) {

                requestBuilder.url(url)

                        .post(params.getRequestBody());

            } else {

                requestBuilder.url(url)

                        .post(formBuilder.build());

            }


            response = NetKit.client().newCall(requestBuilder.build()).execute();

            if (!response.isSuccessful()) {
                responseValue.httpCode = response.code();
                responseValue.setErrorMsg(response.message());

                return responseValue;

            }


            ResponseBody body = response.body();

            if (body == null) {
                responseValue.httpCode = response.code();
                responseValue.setErrorMsg("response body is null");

                return responseValue;

            }


            responseValue.data = parser.parse(body.string());


            return responseValue;


        } catch (final Throwable e) {

            e.printStackTrace();
            if (response != null) {
                responseValue.httpCode = response.code();
            }
            responseValue.setErrorMsg(e.getMessage());

            return responseValue;

        } finally {
            StreamUtils.closeStreams(response);
        }


    }

}