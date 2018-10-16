package com.lch.netkit.v2.okinterceptor;

import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.lch.netkit.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.internal.platform.Platform;

public final class LogInterceptorFactory {

    public static Interceptor newLogInterceptor(boolean logEnable) {
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
                .loggable(logEnable)
                .setLevel(Level.BASIC)
                .tag("NetKit")
                .log(Platform.INFO)
                .request("NetKit-Request")
                .response("NetKit-Response")
                .addHeader("version", BuildConfig.VERSION_NAME)
                .build();

        return loggingInterceptor;
    }
}
