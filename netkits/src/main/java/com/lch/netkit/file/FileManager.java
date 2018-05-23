package com.lch.netkit.file;


import android.support.annotation.NonNull;

import com.lch.netkit.common.mvc.ResponseValue;
import com.lch.netkit.file.helper.DownloadFileParams;
import com.lch.netkit.file.helper.FileTransferCallback;
import com.lch.netkit.file.helper.UploadFileParams;
import com.lch.netkit.file.transfer.FileTransfer;
import com.lch.netkit.file.transfer.impl.FileTransferImpl;
import com.lch.netkit.string.Parser;

import java.io.File;

/**
 * 文件传输管理器。
 */
public class FileManager extends FileTransfer {


    private FileTransfer mBBTFileTransfer = null;

    public FileManager() {
        mBBTFileTransfer = new FileTransferImpl();
    }


    /**
     * 上传文件。
     *
     * @param fileParams 上传文件参数。
     * @param listener   传输监听器。
     * @return requestID 如果成功；否则返回null。
     */
    @Override
    public <T> String uploadFile(UploadFileParams fileParams, @NonNull final Parser<T> parser, @NonNull final FileTransferCallback<T> listener) {
        return chooseFileHelper(fileParams.getServerType()).uploadFile(fileParams, parser, listener);
    }

    /**
     * 下载文件。
     *
     * @param fileParams 下载文件参数。
     * @param listener   传输监听器。
     * @return requestID 如果成功；否则返回null。
     */
    @Override
    public String downloadFile(DownloadFileParams fileParams, @NonNull final FileTransferCallback<File> listener) {
        return mBBTFileTransfer.downloadFile(fileParams, listener);

    }


    @NonNull
    @Override
    public <T> ResponseValue<T> uploadFileSync(UploadFileParams fileParams, @NonNull Parser<T> parser) {
        return mBBTFileTransfer.uploadFileSync(fileParams, parser);
    }

    @NonNull
    @Override
    public ResponseValue<File> downloadFileSync(DownloadFileParams fileParams) {
        return mBBTFileTransfer.downloadFileSync(fileParams);
    }


    private FileTransfer chooseFileHelper(UploadFileParams.ServerType serverType) {
        return mBBTFileTransfer;
    }
}
