package com.lch.netkit.common.tool;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

/**
 * Created by bbt-team on 2017/12/6.
 */

public final class VF {

    public static <T> T f(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static <T> T f(View view, int id) {
        return (T) view.findViewById(id);
    }

    public static <T> T f(Dialog dialog, int id) {
        return (T) dialog.findViewById(id);
    }
}
