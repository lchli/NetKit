package com.lch.netkit.common.mvc;

import android.support.annotation.Nullable;

/**
 * Created by bbt-team on 2018/2/5.
 */

public interface ControllerCallback<DATA> {

    public abstract void onSuccess(@Nullable DATA data);

    public abstract void onError(int code, String msg);

}
