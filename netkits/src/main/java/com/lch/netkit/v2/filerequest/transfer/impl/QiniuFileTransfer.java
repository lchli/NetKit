package com.lch.netkit.v2.filerequest.transfer.impl;
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

import com.lch.netkit.v2.common.Cancelable;
import com.lch.netkit.v2.common.NetKitException;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.filerequest.DownloadFileCallback;
import com.lch.netkit.v2.filerequest.DownloadFileParams;
import com.lch.netkit.v2.filerequest.FileOptions;
import com.lch.netkit.v2.filerequest.QiNiuParam;
import com.lch.netkit.v2.filerequest.UploadFileCallback;
import com.lch.netkit.v2.filerequest.UploadFileParams;
import com.lch.netkit.v2.filerequest.transfer.FileTransfer;
import com.lch.netkit.v2.parser.Parser;
import com.lch.netkit.v2.util.NetworkLog;
import com.lch.netkit.v2.util.ShareConstants;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.lch.netkit.v2.util.CallbackUtil.onError;
import static com.lch.netkit.v2.util.CallbackUtil.onProgress;
import static com.lch.netkit.v2.util.CallbackUtil.onSuccess;

/**
 * 七牛文件传输器实现。
 */
public class QiniuFileTransfer implements FileTransfer {
    private static final String TAG = "QiniuFileTransfer";

    private UploadManager qiNiuUploadManager;

    public QiniuFileTransfer(Context context) {
        try {
            FileRecorder fileRecorder = new FileRecorder(new File(context.getExternalCacheDir(), "qiNiuRecord").getAbsolutePath());
            qiNiuUploadManager = new UploadManager(fileRecorder);
        } catch (Exception e) {
            e.printStackTrace();
            qiNiuUploadManager = new UploadManager();
        }
    }

    @Override
    public <T> Cancelable uploadFile(@NonNull final UploadFileParams fileParams, @NonNull final Parser<T> parser, final UploadFileCallback<T> listener) {

        List<FileOptions> fileIter = fileParams.files();
        if (fileIter.isEmpty()) {
            onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "files is empty.", listener);
            return null;
        }

        final Cancelable cancelable = new Cancelable();

        FileOptions fileOptions = fileIter.get(0);

        final QiNiuParam qiniuParam = fileParams.getQiNiuParam();

        UploadOptions uploadOptions = new UploadOptions(qiniuParam.getUploadOptionParam(), qiniuParam.getMimeType(), qiniuParam.isCheckCrc(), new UpProgressHandler() {

            double previousPercent = 0;

            @Override
            public void progress(String key, final double percent) {

                if (percent < 1.0F && percent - previousPercent < ShareConstants.UPDATE_PROGRESS_GAP) {
                    return;
                }
                previousPercent = percent;

                onProgress(percent, listener);

            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return cancelable.isCanceled();
            }
        });

        if (fileOptions.getFile() != null) {
            qiNiuUploadManager.put(fileOptions.getFile(), fileOptions.getFileKey(), qiniuParam.getQiniuToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    NetworkLog.e(TAG, info + "\n" + response);

                    if (info.isOK()) {
                        try {
                            onSuccess(info.statusCode + "", parser.parse(response.toString()), listener);
                        } catch (Throwable e) {
                            e.printStackTrace();

                            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                                onError(((NetKitException) e).getHttpCode(), e.getMessage() + "", listener);
                            } else {
                                onError(info.statusCode + "", e.getMessage() + "", listener);
                            }
                        }

                    } else {
                        onError(info.statusCode + "", info.error, listener);
                    }

                }
            }, uploadOptions);
        } else if (fileOptions.getFilePath() != null) {
            qiNiuUploadManager.put(fileOptions.getFilePath(), fileOptions.getFileKey(), qiniuParam.getQiniuToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    NetworkLog.e(TAG, info + "\n" + response);

                    if (info.isOK()) {
                        try {
                            onSuccess(info.statusCode + "", parser.parse(response.toString()), listener);
                        } catch (Throwable e) {
                            e.printStackTrace();

                            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                                onError(((NetKitException) e).getHttpCode(), e.getMessage() + "", listener);
                            } else {
                                onError(info.statusCode + "", e.getMessage() + "", listener);
                            }
                        }

                    } else {
                        onError(info.statusCode + "", info.error, listener);
                    }


                }
            }, uploadOptions);
        } else if (fileOptions.getFileBytes() != null) {
            qiNiuUploadManager.put(fileOptions.getFileBytes(), fileOptions.getFileKey(), qiniuParam.getQiniuToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    NetworkLog.e(TAG, info + "\n" + response);

                    if (info.isOK()) {
                        try {
                            onSuccess(info.statusCode + "", parser.parse(response.toString()), listener);
                        } catch (Throwable e) {
                            e.printStackTrace();

                            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                                onError(((NetKitException) e).getHttpCode(), e.getMessage() + "", listener);
                            } else {
                                onError(info.statusCode + "", e.getMessage() + "", listener);
                            }
                        }

                    } else {
                        onError(info.statusCode + "", info.error, listener);
                    }


                }
            }, uploadOptions);

        } else {
            onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "upload file is null.", listener);
        }

        return cancelable;
    }


    @NonNull
    @Override
    public <T> NetworkResponse<T> syncUploadFile(UploadFileParams fileParams, final Parser<T> parser) {
        final NetworkResponse<T> networkResponse = new NetworkResponse<>();

        List<FileOptions> fileIter = fileParams.files();
        if (fileIter.isEmpty()) {
            networkResponse.setErrorMsg("files is empty.");
            return networkResponse;
        }

        FileOptions fileOptions = fileIter.get(0);

        final QiNiuParam qiniuParam = fileParams.getQiNiuParam();

        UploadOptions uploadOptions = new UploadOptions(qiniuParam.getUploadOptionParam(), qiniuParam.getMimeType(), qiniuParam.isCheckCrc(), new UpProgressHandler() {

            double previousPercent = 0;

            @Override
            public void progress(String key, final double percent) {

                if (percent < 1.0F && percent - previousPercent < ShareConstants.UPDATE_PROGRESS_GAP) {
                    return;
                }
                previousPercent = percent;

            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        });

        if (fileOptions.getFile() != null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            qiNiuUploadManager.put(fileOptions.getFile(), fileOptions.getFileKey(), qiniuParam.getQiniuToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    NetworkLog.e(TAG, info + "\n" + response);
                    networkResponse.httpCode = info.statusCode + "";

                    if (info.isOK()) {
                        try {
                            networkResponse.data = parser.parse(response.toString());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                                networkResponse.httpCode = ((NetKitException) e).getHttpCode();
                            }
                            networkResponse.setErrorMsg(e.getMessage());
                        }

                    } else {
                        networkResponse.setErrorMsg(info.error);
                    }

                    countDownLatch.countDown();

                }
            }, uploadOptions);

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if (fileOptions.getFilePath() != null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            qiNiuUploadManager.put(fileOptions.getFilePath(), fileOptions.getFileKey(), qiniuParam.getQiniuToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    NetworkLog.e(TAG, info + "\n" + response);
                    networkResponse.httpCode = info.statusCode + "";

                    if (info.isOK()) {
                        try {
                            networkResponse.data = parser.parse(response.toString());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                                networkResponse.httpCode = ((NetKitException) e).getHttpCode();
                            }
                            networkResponse.setErrorMsg(e.getMessage());
                        }

                    } else {
                        networkResponse.setErrorMsg(info.error);
                    }

                    countDownLatch.countDown();

                }
            }, uploadOptions);

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } else if (fileOptions.getFileBytes() != null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            qiNiuUploadManager.put(fileOptions.getFileBytes(), fileOptions.getFileKey(), qiniuParam.getQiniuToken(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    NetworkLog.e(TAG, info + "\n" + response);
                    networkResponse.httpCode = info.statusCode + "";

                    if (info.isOK()) {
                        try {
                            networkResponse.data = parser.parse(response.toString());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                                networkResponse.httpCode = ((NetKitException) e).getHttpCode();
                            }
                            networkResponse.setErrorMsg(e.getMessage());
                        }

                    } else {
                        networkResponse.setErrorMsg(info.error);
                    }

                    countDownLatch.countDown();

                }
            }, uploadOptions);

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            networkResponse.setErrorMsg("upload file is null.");
        }


        return networkResponse;
    }

    @Override
    public Cancelable downloadFile(DownloadFileParams fileParams, DownloadFileCallback listener) {
        throw new UnsupportedOperationException("qi niu do not support download file now.");
    }

    @NonNull
    @Override
    public NetworkResponse<File> syncDownloadFile(DownloadFileParams fileParams) {
        throw new UnsupportedOperationException("qi niu do not support download file now.");
    }
}
