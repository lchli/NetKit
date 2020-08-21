package com.lch.netkit.v2.util;



import android.support.annotation.Nullable;

import java.net.URL;

public final class UrlUtils {

    @Nullable
    public static String getHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
