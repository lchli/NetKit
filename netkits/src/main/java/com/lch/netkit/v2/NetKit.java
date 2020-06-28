package com.lch.netkit.v2;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.lch.netkit.v2.apirequest.ApiRequest;
import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.filerequest.FileRequest;
import com.lch.netkit.v2.okinterceptor.ApiInterceptor;
import com.lch.netkit.v2.okinterceptor.GzipRequestInterceptor;
import com.lch.netkit.v2.okinterceptor.LogInterceptorFactory;
import com.lch.netkit.v2.util.UiThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.Util;

/**
 * 网络请求入口类。
 */
public final class NetKit {

    private static final String TAG = "NetKit";
    private static final int NET_TIME_OUT = 30;
    private static final ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("NetKit Dispatcher", false));

    private static FileRequest fileRequest;
    private static ApiRequest apiRequest;

    private static boolean logEnable = false;
    private static OkHttpClient client;
    private static ApiInterceptor apiInterceptor;

    /**
     * 初始化。使用默认的okhttp builder。
     *
     * @param context 上下文
     */
    public static void init(@NonNull Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(NET_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(NET_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(NET_TIME_OUT, TimeUnit.SECONDS);

        init(context, builder);
    }

    /**
     * 初始化。使用自定义的okhttp builder。
     *
     * @param context 上下文
     * @param builder 自定义的okhttp builder。
     */
    public static void init(@NonNull Context context, @NonNull OkHttpClient.Builder builder) {
        if (client != null) {
            return;
        }

        if (context instanceof Activity) {
            context = context.getApplicationContext();
        }

        builder.addInterceptor(new GzipRequestInterceptor());
        builder.addInterceptor(LogInterceptorFactory.newLogInterceptor(logEnable));

        client = builder.build();
        fileRequest = new FileRequest(context);
        apiRequest = new ApiRequest();
    }

    /**
     * 获取http client
     *
     * @return
     */
    public static OkHttpClient client() {
        return client;
    }

    /**
     * 获取用于文件传输请求的对象。
     *
     * @return
     */
    public static FileRequest fileRequest() {
        return fileRequest;
    }

    /**
     * 获取用于普通api请求的对象。
     *
     * @return
     */
    public static ApiRequest apiRequest() {
        return apiRequest;
    }


    /**
     * 设置是否显示http请求日志。线上环境建议关闭。
     *
     * @param enable
     */
    public static void setLogEnable(boolean enable) {
        logEnable = enable;
    }

    /**
     * 设置请求拦截器。可用于添加公共参数等。
     *
     * @param apiInterceptor
     */
    public static void setApiInterceptor(ApiInterceptor apiInterceptor) {
        NetKit.apiInterceptor = apiInterceptor;
    }

    public static final class Internal {

        public static void runInUI(Runnable r) {
            UiThread.run(r);
        }

        public static void runAsync(Runnable r) {
            try {
                executorService.execute(r);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public static boolean logEnable() {
            return logEnable;
        }

        public static ApiRequestParams interceptApiRequestParams(ApiRequestParams requestParams) {
            if (apiInterceptor != null) {
                ApiRequestParams after = apiInterceptor.interceptApiRequestParams(requestParams);
                return after != null ? after : requestParams;
            } else {
                return requestParams;
            }
        }

        public static String interceptResponse(String responseString) {
            if (apiInterceptor != null) {
                return apiInterceptor.interceptResponse(responseString);
            } else {
                return responseString;
            }
        }
    }


}
