package com.lch.netkit.common.mvc;

import com.lch.netkit.common.tool.UiHandler;

/**
 * Created by Administrator on 2018/10/17.
 */

public final class CtrlCbUtils {

    public static void onError(final ControllerCallback controllerCallback, final int code, final String errMsg) {
        if (controllerCallback == null) {
            return;
        }

        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                controllerCallback.onError(code, errMsg);
            }
        });
    }

    public static void onError(final ControllerCallback controllerCallback, final String errMsg) {
        if (controllerCallback == null) {
            return;
        }

        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                controllerCallback.onError(0, errMsg);
            }
        });
    }


    public static void onSuccess(final ControllerCallback controllerCallback, final Object data) {
        if (controllerCallback == null) {
            return;
        }

        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                controllerCallback.onSuccess(data);
            }
        });
    }
}
