package com.lch.netkit.file.helper;

import com.lch.netkit.common.mvc.ControllerCallback;

/**
 * Created by lichenghang on 2018/5/23.
 */

public interface FileTransferCallback<DATA> extends ControllerCallback<DATA> {

    void onProgress(double percent);
}
