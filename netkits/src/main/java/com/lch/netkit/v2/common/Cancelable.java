package com.lch.netkit.v2.common;

import okhttp3.Call;

public class Cancelable {
    private Call call;
    private volatile boolean isCanceled = false;

    public void setCall(Call call) {
        this.call = call;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
            call = null;
        }

        isCanceled = true;
    }


}
