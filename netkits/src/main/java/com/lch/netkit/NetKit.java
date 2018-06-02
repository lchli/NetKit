package com.lch.netkit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.apkfuns.logutils.LogUtils;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.lch.netkit.common.tool.ContextProvider;
import com.lch.netkit.file.FileManager;
import com.lch.netkit.string.StringRequest;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;

/**
 * Created by lichenghang on 2017/10/3.
 */

public final class NetKit {
    private static final String TAG = "NetKit";
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private static FileManager fileManager;
    private static StringRequest stringRequest;
    private static boolean logEnable = false;
    private static OkHttpClient client;

    public static void init(Context context) {
        if (client != null) {
            return;
        }

        ContextProvider.initContext(context);

        client = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor.Builder()
                .loggable(logEnable)
                .setLevel(Level.BASIC)
                .tag(TAG)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .addHeader("version", BuildConfig.VERSION_NAME)
                .build()).build();

        fileManager = new FileManager();
        stringRequest = new StringRequest();
    }

    public static OkHttpClient client() {
        return client;
    }

    public static void setClient(OkHttpClient client) {
        NetKit.client = client;
    }

    public static FileManager fileRequest() {
        return fileManager;
    }

    public static StringRequest stringRequest() {
        return stringRequest;

    }

    public static void runAsync(Runnable r) {
        client().dispatcher().executorService().execute(r);
    }

    public static void runInUI(Runnable r) {
        handler.post(r);
    }

    public static void setLogEnable(boolean b) {
        LogUtils.getLogConfig().configAllowLog(b);
        logEnable = b;
    }


}
