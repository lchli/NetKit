package com.lch.netkit.v2.filerequest;
/*
 * Copyright (C) 2017 BabyTree-inc.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lch.netkit.v2.common.Cancelable;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.filerequest.transfer.FileTransfer;
import com.lch.netkit.v2.filerequest.transfer.impl.BBTFileTransfer;
import com.lch.netkit.v2.filerequest.transfer.impl.QiniuFileTransfer;
import com.lch.netkit.v2.parser.ModelParser;
import com.lch.netkit.v2.parser.Parser;

import java.io.File;


/**
 * 文件传输管理器。
 */
public class FileRequest {

    private FileTransfer mBBTFileTransfer;

    private FileTransfer mQiniuFileTransfer;

    public FileRequest(Context context) {
        mBBTFileTransfer = new BBTFileTransfer();
        mQiniuFileTransfer = new QiniuFileTransfer(context);
    }

    /**
     * 上传文件。
     *
     * @param fileParams 参数。
     * @param parser     响应内容的解析器。默认解析器{@see com.babytree.baf.network.parser.Parsers#JSON}，{@see com.babytree.baf.network.parser.Parsers#STRING}
     * @param listener   回调。
     * @param <T>
     * @return 可取消请求的对象。
     */
    @Nullable
    public <T> Cancelable uploadFile(@NonNull UploadFileParams fileParams, @NonNull final Parser<T> parser, final UploadFileCallback<T> listener) {
        return chooseFileTransfer(fileParams.getServerType()).uploadFile(fileParams, parser, listener);

    }

    @Nullable
    public <T> Cancelable uploadFile(@NonNull UploadFileParams fileParams, @NonNull final Class<T> resultClass, final UploadFileCallback<T> listener) {
        return chooseFileTransfer(fileParams.getServerType()).uploadFile(fileParams, new ModelParser<>(resultClass), listener);

    }

    /**
     * 下载文件。
     *
     * @param fileParams 参数。
     * @param listener   回调。
     * @return 可取消请求的对象。
     */
    @Nullable
    public Cancelable downloadFile(@NonNull DownloadFileParams fileParams, final DownloadFileCallback listener) {
        return mBBTFileTransfer.downloadFile(fileParams, listener);

    }


    @NonNull
    public <T> NetworkResponse<T> syncUploadFile(UploadFileParams fileParams, Parser<T> parser) {
        return chooseFileTransfer(fileParams.getServerType()).syncUploadFile(fileParams, parser);
    }


    @NonNull
    public <T> NetworkResponse<T> syncUploadFile(UploadFileParams fileParams, Class<T> resultClass) {
        return chooseFileTransfer(fileParams.getServerType()).syncUploadFile(fileParams, new ModelParser<>(resultClass));
    }

    @NonNull
    public NetworkResponse<File> syncDownloadFile(DownloadFileParams fileParams) {
        return mBBTFileTransfer.syncDownloadFile(fileParams);
    }


    private FileTransfer chooseFileTransfer(UploadFileParams.ServerType serverType) {
        switch (serverType) {
            case QI_NIU:
                return mQiniuFileTransfer;
            default:
                return mBBTFileTransfer;
        }
    }
}
