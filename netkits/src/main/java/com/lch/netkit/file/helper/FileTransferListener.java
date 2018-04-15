package com.lch.netkit.file.helper;

import com.lch.netkit.common.mvc.MvcError;

/**
 * 文件传输回调。
 * Created by liyonghao on 27/07/2017.
 */

public interface FileTransferListener {

    void onResponse(FileResponse response);

    void onError(MvcError error);

    void onProgress(double percent);


    FileTransferListener DEF_LISTENER = new FileTransferListener() {
        @Override
        public void onResponse(FileResponse response) {

        }

        @Override
        public void onError(MvcError error) {

        }

        @Override
        public void onProgress(double percent) {

        }
    };
}
