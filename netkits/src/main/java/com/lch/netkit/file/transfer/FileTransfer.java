package com.lch.netkit.file.transfer;


import android.support.annotation.NonNull;

import com.lch.netkit.common.mvc.ResponseValue;
import com.lch.netkit.file.helper.DownloadFileParams;
import com.lch.netkit.file.helper.FileTransferCallback;
import com.lch.netkit.file.helper.FileTransferState;
import com.lch.netkit.file.helper.UploadFileParams;
import com.lch.netkit.string.Parser;

import java.io.File;
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


    public abstract <T> String uploadFile(UploadFileParams fileParams, @NonNull final Parser<T> parser, @NonNull final FileTransferCallback<T> listener);

    @NonNull
    public abstract <T> ResponseValue<T> uploadFileSync(final UploadFileParams fileParams, @NonNull final Parser<T> parser);

    public abstract String downloadFile(DownloadFileParams fileParams, @NonNull final FileTransferCallback<File> listener);

    @NonNull
    public abstract ResponseValue<File> downloadFileSync(final DownloadFileParams fileParams);
}
