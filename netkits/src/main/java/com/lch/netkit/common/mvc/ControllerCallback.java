package com.lch.netkit.common.mvc;

import android.support.annotation.NonNull;

/**
 * Created by bbt-team on 2018/2/5.
 */

public interface ControllerCallback<DATA> {

    void onComplete(@NonNull ResponseValue<DATA> responseValue);

}
