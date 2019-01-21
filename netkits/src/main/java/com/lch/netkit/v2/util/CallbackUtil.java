package com.lch.netkit.v2.util;

import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.filerequest.UploadFileCallback;

public final class CallbackUtil {

    public static void onSuccess(final String httpCode, final Object data, final RequestCallback callback) {
        if (callback != null) {
            if (callback.isCallbackInUiThread()) {
                NetKit.Internal.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(httpCode, data);
                    }
                });
            } else {
                callback.onSuccess(httpCode, data);
            }
        }

    }


    public static void onError(final String httpCode, final String err, final RequestCallback callback) {
        if (callback != null) {
            if (callback.isCallbackInUiThread()) {
                NetKit.Internal.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(httpCode, err);
                    }
                });
            } else {
                callback.onError(httpCode, err);
            }
        }

    }

    public static void onProgress(final double percent, final UploadFileCallback callback) {
        if (callback != null) {
            if (callback.isCallbackInUiThread()) {
                NetKit.Internal.runInUI(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProgress(percent);
                    }
                });
            } else {
                callback.onProgress(percent);
            }
        }

    }

}
