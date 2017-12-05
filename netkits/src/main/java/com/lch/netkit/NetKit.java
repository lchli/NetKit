package com.lch.netkit;

import android.os.Handler;
import android.os.Looper;

import com.apkfuns.logutils.LogUtils;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
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
    private static final FileManager fileManager = new FileManager();
    private static final StringRequest stringRequest = new StringRequest();
    private static boolean logEnable = false;
    private static OkHttpClient client;

    public static void init() {
        if (client != null) {
            return;
        }

        client = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor.Builder()
                .loggable(logEnable)
                .setLevel(Level.BASIC)
                .tag(TAG)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .addHeader("version", BuildConfig.VERSION_NAME)
                .build()).build();
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
