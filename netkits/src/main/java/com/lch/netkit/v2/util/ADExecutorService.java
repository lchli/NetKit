package com.lch.netkit.v2.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ADExecutorService {

    private static final ExecutorService INIT_EXECUTOR = Executors.newFixedThreadPool(5);

    public static void run(Runnable r) {
        try {
            INIT_EXECUTOR.execute(r);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
