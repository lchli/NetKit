package com.lch.netkit.file.transfer;


import com.lch.netkit.NetKit;
import com.lch.netkit.file.helper.DownloadFileParams;
import com.lch.netkit.file.helper.FileResponse;
import com.lch.netkit.file.helper.FileTransferListener;
import com.lch.netkit.file.helper.FileTransferState;
import com.lch.netkit.file.helper.NetworkError;
import com.lch.netkit.file.helper.UploadFileParams;

import java.util.HashMap;
import java.util.Map;

public abstract class FileTransfer {

    protected static final Map<String, FileTransferState> calls = new HashMap<>();

    public static void cancel(String requestID) {
        if (requestID == null) {
            return;
        }

        FileTransferState state = calls.get(requestID);
        if (state != null) {
            if (state.getCall() != null) {
                state.getCall().cancel();
            }
            state.setCanceled(true);
        }
    }

    public static FileTransferState getFileTransferState(String requestID) {
        if (requestID == null) {
            return null;
        }
        return calls.get(requestID);
    }

    public static void onError(final NetworkError error, final FileTransferListener listener) {
        NetKit.runInUI(new Runnable() {
            @Override
            public void run() {
                listener.onError(error);
            }
        });
    }

    public static void onResponse(final FileResponse data, final FileTransferListener listener) {
        NetKit.runInUI(new Runnable() {
            @Override
            public void run() {
                listener.onResponse(data);
            }
        });
    }

    public static void onProgress(final double percent, final FileTransferListener listener) {
        NetKit.runInUI(new Runnable() {
            @Override
            public void run() {
                listener.onProgress(percent);
            }
        });
    }

    public abstract String uploadFile(UploadFileParams fileParams, final FileTransferListener listener);

    public abstract String downloadFile(DownloadFileParams fileParams, final FileTransferListener listener);
}
