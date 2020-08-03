package com.lch.netkit.v2.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by bbt-team on 2017/8/2.
 */

public class UiThread {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void run(Runnable r) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            r.run();
        } else {
            handler.post(r);
        }
    }

    public static void run(Runnable r, long delayMills) {
        handler.postDelayed(r, delayMills);
    }
}
