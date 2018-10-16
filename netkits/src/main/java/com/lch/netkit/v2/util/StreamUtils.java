package com.lch.netkit.v2.util;

import java.io.Closeable;

/**
 * Created by bbt-team on 2017/8/4.
 */

public final class StreamUtils {

    public static void closeStreams(Closeable... closeables) {
        try {
            if (closeables == null) {
                return;
            }

            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
