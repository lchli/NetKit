package com.lch.netkit.v2.common;

import android.support.annotation.Nullable;

public abstract class RequestCallback<DATA> {

    public boolean isCallbackInUiThread() {
        return true;
    }

    public abstract void onSuccess(String httpCode, @Nullable DATA data);

    public abstract void onError(String httpCode, String msg);

}
