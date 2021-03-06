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

import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.common.Cancelable;
import com.lch.netkit.v2.common.NetKitException;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.filerequest.DownloadFileCallback;
import com.lch.netkit.v2.filerequest.FileOptions;
import com.lch.netkit.v2.filerequest.UploadFileCallback;
import com.lch.netkit.v2.filerequest.transfer.FileTransfer;
import com.lch.netkit.v2.parser.Parser;
import com.lch.netkit.v2.util.NetworkLog;
import com.lch.netkit.v2.util.ShareConstants;
import com.lch.netkit.v2.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

import static com.lch.netkit.v2.util.CallbackUtil.onError;
import static com.lch.netkit.v2.util.CallbackUtil.onProgress;
import static com.lch.netkit.v2.util.CallbackUtil.onSuccess;

/**
 * 上传地址由url指定的文件传输器实现。
 */
public class BBTFileTransfer implements FileTransfer {

    private static final String TAG = "BBTFileTransfer";
    private static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private static final int DOWNLOAD_BUF = 1024;


    @Override
    public <T> Cancelable uploadFile(@NonNull final ApiRequestParams fileParamsRaw, @NonNull final Parser<T> parser, final UploadFileCallback<T> listener) {

        final List<FileOptions> filesIter = fileParamsRaw.files();
        if (filesIter.isEmpty()) {
            onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "files is empty.", listener);
            return null;
        }

        if (TextUtils.isEmpty(fileParamsRaw.getUrl())) {
            onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "uploadUrl is empty.", listener);
            return null;
        }

        final Cancelable cancelable = new Cancelable();

        runAsync(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                String responseCode = ShareConstants.HTTP_ERR_CODE_UNKNOWN;

                try {

                    final ApiRequestParams fileParams = NetKit.Internal.interceptApiRequestParams(fileParamsRaw);

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);

                    Map<String, String> textParamsIter = fileParams.params();
                    for (Map.Entry<String, String> entry : textParamsIter.entrySet()) {
                        if (entry.getKey() != null) {
                            builder.addFormDataPart(entry.getKey(), entry.getValue());
                        }
                    }

                    for (FileOptions fileOpt : filesIter) {

                        if (fileOpt.getFileKey() == null) {
                            onError(responseCode, "you must specify a file key to upload.", listener);

                            return;
                        }

                        if (fileOpt.getFile() != null) {
                            builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFile()));
                        } else if (fileOpt.getFilePath() != null) {
                            builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, new File(fileOpt.getFilePath())));
                        } else if (fileOpt.getFileBytes() != null) {
                            builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFileBytes()));
                        }
                    }

                    Request.Builder requestBuilder = new Request.Builder();

                    Map<String, String> headers = fileParams.headers();
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        if (header.getKey() != null) {
                            requestBuilder.addHeader(header.getKey(), header.getValue());
                        }
                    }

                    RequestBody requestBody = builder.build();

                    Request request = requestBuilder
                            .url(fileParams.getUrl())
                            .post(new BBTFileTransfer.CountingRequestBody(requestBody, new BBTFileTransfer.CountingRequestBody.Listener() {
                                float previousPercent = 0;

                                @Override
                                public void onRequestProgress(final long bytesWritten, final long contentLength) {
                                    final float percent = formatPercent(bytesWritten, contentLength);

                                    if (bytesWritten < contentLength && (percent - previousPercent) < ShareConstants.UPDATE_PROGRESS_GAP) {
                                        return;
                                    }
                                    previousPercent = percent;

                                    onProgress(percent, listener);

                                }
                            }))
                            .build();

                    if (cancelable.isCanceled()) {
                        onError(responseCode, "canceled", listener);
                        return;
                    }

                    final Call call = NetKit.client().newCall(request);
                    cancelable.setCall(call);

                    response = call.execute();
                    responseCode = response.code() + "";

                    if (!response.isSuccessful()) {
                        onError(responseCode, response.message(), listener);
                        return;
                    }

                    ResponseBody body = response.body();
                    if (body == null) {
                        onError(responseCode, "response body is null.", listener);
                        return;
                    }

                    onSuccess(responseCode, parser.parse(NetKit.Internal.interceptResponse(body.string())), listener);

                } catch (final Throwable e) {
                    e.printStackTrace();

                    if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                        responseCode = ((NetKitException) e).getHttpCode();
                    }

                    onError(responseCode, e.getMessage() + "", listener);

                } finally {
                    StreamUtils.closeStreams(response);
                }

            }
        });

        return cancelable;
    }

    @NonNull
    @Override
    public <T> NetworkResponse<T> syncUploadFile(ApiRequestParams fileParams, Parser<T> parser) {
        NetworkResponse<T> networkResponse = new NetworkResponse<>();
        Response response = null;

        try {
            fileParams = NetKit.Internal.interceptApiRequestParams(fileParams);

            final List<FileOptions> filesIter = fileParams.files();
            if (filesIter.isEmpty()) {
                networkResponse.setErrorMsg("files is empty.");
                return networkResponse;
            }

            if (TextUtils.isEmpty(fileParams.getUrl())) {
                networkResponse.setErrorMsg("uploadUrl is empty.");
                return networkResponse;
            }

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            Map<String, String> textParamsIter = fileParams.params();
            for (Map.Entry<String, String> entry : textParamsIter.entrySet()) {
                if (entry.getKey() != null) {
                    builder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            for (FileOptions fileOpt : filesIter) {

                if (fileOpt.getFileKey() == null) {
                    networkResponse.setErrorMsg("you must specify a file key to upload.");
                    return networkResponse;
                }

                if (fileOpt.getFile() != null) {
                    builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFile()));
                } else if (fileOpt.getFilePath() != null) {
                    builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, new File(fileOpt.getFilePath())));
                } else if (fileOpt.getFileBytes() != null) {
                    builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFileBytes()));
                }
            }

            Request.Builder requestBuilder = new Request.Builder();

            Map<String, String> headers = fileParams.headers();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() != null) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }

            RequestBody requestBody = builder.build();

            Request request = requestBuilder
                    .url(fileParams.getUrl())
                    .post(new CountingRequestBody(requestBody, new BBTFileTransfer.CountingRequestBody.Listener() {
                        float previousPercent = 0;

                        @Override
                        public void onRequestProgress(final long bytesWritten, final long contentLength) {
                            final float percent = formatPercent(bytesWritten, contentLength);

                            if (bytesWritten < contentLength && (percent - previousPercent) < ShareConstants.UPDATE_PROGRESS_GAP) {
                                return;
                            }
                            previousPercent = percent;


                        }
                    }))
                    .build();

            final Call call = NetKit.client().newCall(request);

            response = call.execute();

            networkResponse.httpCode = response.code() + "";

            if (!response.isSuccessful()) {
                networkResponse.setErrorMsg(response.message());
                return networkResponse;
            }

            ResponseBody body = response.body();
            if (body == null) {
                networkResponse.setErrorMsg("response body is null.");
                return networkResponse;
            }
            networkResponse.data = parser.parse(NetKit.Internal.interceptResponse(body.string()));

            return networkResponse;

        } catch (final Throwable e) {
            e.printStackTrace();

            if (e instanceof NetKitException && ((NetKitException) e).getHttpCode() != null) {
                networkResponse.httpCode = ((NetKitException) e).getHttpCode();
            }

            networkResponse.setErrorMsg(e.getMessage());

            return networkResponse;

        } finally {
            StreamUtils.closeStreams(response);
        }
    }

    @Override
    public Cancelable downloadFile(@NonNull final ApiRequestParams fileParamsRaw, final DownloadFileCallback listener) {

        if (TextUtils.isEmpty(fileParamsRaw.getUrl())) {
            onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "file url is empty.", listener);
            return null;
        }

        if (TextUtils.isEmpty(fileParamsRaw.getDownloadFileSavePath())) {
            onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "file save dir is invalid.", listener);
            return null;
        }

        final Cancelable cancelable = new Cancelable();

        runAsync(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                FileOutputStream fos = null;
                Response response = null;
                String code = ShareConstants.HTTP_ERR_CODE_UNKNOWN;

                try {
                    final ApiRequestParams fileParams = NetKit.Internal.interceptApiRequestParams(fileParamsRaw);

                    final Request.Builder requestBuilder = new Request.Builder();

                    String finalUrl = fileParams.getUrl();

                    if (!finalUrl.contains("?")) {
                        finalUrl += "?";
                    }

                    if (!finalUrl.endsWith("?")) {
                        finalUrl += "&";
                    }

                    StringBuilder sb = new StringBuilder(finalUrl);

                    Map<String, String> paramIter = fileParams.params();

                    for (Map.Entry<String, String> entry : paramIter.entrySet()) {
                        if (entry.getKey() != null) {
                            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                        }
                    }

                    finalUrl = sb.toString();

                    Map<String, String> headers = fileParams.headers();
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        if (header.getKey() != null) {
                            requestBuilder.addHeader(header.getKey(), header.getValue());
                        }
                    }

                    long totalLen = getUrlContentLength(fileParams.getUrl());
                    log("totalLen=%d", totalLen);

                    final String saveFileName = fileParams.getDownloadFileSavePath();
                    File dir = new File(saveFileName).getParentFile();
                    if (dir != null) {
                        createFileIfNotExist(dir.getAbsolutePath());
                    }

                    final File saveFile = createFileIfNotExist(saveFileName);
                    log("saveFile=%s", saveFile.getAbsolutePath());

                    long downloadedLength = saveFile.length();
                    log("downloadLength=%d", downloadedLength);

                    boolean append;
                    if (totalLen == -1 || downloadedLength >= totalLen) {
                        log("do not use append mode download,url=%s", fileParams.getUrl());
                        append = false;
                        downloadedLength = 0;
                    } else {//支持断点。
                        log("use append mode download,url=%s", fileParams.getUrl());
                        append = true;
                        requestBuilder.addHeader("RANGE", "bytes=" + downloadedLength + "-");
                    }

                    Request request = requestBuilder.get()
                            .url(finalUrl)
                            .build();

                    if (cancelable.isCanceled()) {
                        onError(ShareConstants.HTTP_ERR_CODE_UNKNOWN, "canceled", listener);
                        return;
                    }

                    final Call call = NetKit.client().newCall(request);
                    cancelable.setCall(call);

                    response = call.execute();
                    code = response.code() + "";

                    if (!response.isSuccessful()) {
                        onError(code, response.message(), listener);
                        return;
                    }

                    ResponseBody body = response.body();
                    if (body == null) {
                        onError(code, "response body is null.", listener);
                        return;
                    }

                    fos = new FileOutputStream(saveFile, append);
                    is = body.byteStream();

                    byte[] buf = new byte[DOWNLOAD_BUF];
                    float previousPercent = 0;
                    long sum = downloadedLength;
                    int len;

                    onProgress(formatPercent(sum, totalLen), listener);

                    while ((len = is.read(buf)) != -1) {
                        sum += len;
                        fos.write(buf, 0, len);

                        final float percent = formatPercent(sum, totalLen);

                        if (percent - previousPercent < ShareConstants.UPDATE_PROGRESS_GAP) {//只在增量大于总长度一定比例后才通知UI更新，减少UI线程的丢帧率。
                            continue;
                        }
                        previousPercent = percent;

                        onProgress(percent, listener);
                    }

                    onProgress(formatPercent(sum, sum), listener);//assert sum==total.

                    fos.flush();

                    onSuccess(code, saveFile, listener);

                } catch (final Throwable e) {
                    e.printStackTrace();

                    onError(code, e.getMessage() + "", listener);

                } finally {
                    StreamUtils.closeStreams(is, fos, response);
                }
            }
        });

        return cancelable;

    }

    @NonNull
    @Override
    public NetworkResponse<File> syncDownloadFile(ApiRequestParams fileParams) {
        NetworkResponse<File> networkResponse = new NetworkResponse<>();
        InputStream is = null;
        FileOutputStream fos = null;
        Response response = null;

        try {

            fileParams = NetKit.Internal.interceptApiRequestParams(fileParams);

            if (TextUtils.isEmpty(fileParams.getUrl())) {
                networkResponse.setErrorMsg("file url is empty.");
                return networkResponse;
            }

            if (TextUtils.isEmpty(fileParams.getDownloadFileSavePath())) {
                networkResponse.setErrorMsg("file save dir is invalid.");
                return networkResponse;
            }

            final Request.Builder requestBuilder = new Request.Builder();

            String finalUrl = fileParams.getUrl();

            if (!finalUrl.contains("?")) {
                finalUrl += "?";
            }

            if (!finalUrl.endsWith("?")) {
                finalUrl += "&";
            }

            StringBuilder sb = new StringBuilder(finalUrl);

            Map<String, String> paramIter = fileParams.params();

            for (Map.Entry<String, String> entry : paramIter.entrySet()) {
                if (entry.getKey() != null) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
            }

            finalUrl = sb.toString();

            Map<String, String> headers = fileParams.headers();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() != null) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }

            long totalLen = getUrlContentLength(fileParams.getUrl());
            log("totalLen=%d", totalLen);

            final String saveFileName = fileParams.getDownloadFileSavePath();
            File dir = new File(saveFileName).getParentFile();
            if (dir != null) {
                createFileIfNotExist(dir.getAbsolutePath());
            }

            final File saveFile = createFileIfNotExist(saveFileName);
            log("saveFile=%s", saveFile.getAbsolutePath());

            long downloadedLength = saveFile.length();
            log("downloadLength=%d", downloadedLength);

            boolean append;
            if (totalLen == -1 || downloadedLength >= totalLen) {
                log("do not use append mode download,url=%s", fileParams.getUrl());
                append = false;
                downloadedLength = 0;
            } else {//支持断点。
                log("use append mode download,url=%s", fileParams.getUrl());
                append = true;
                requestBuilder.addHeader("RANGE", "bytes=" + downloadedLength + "-");
            }

            Request request = requestBuilder.get()
                    .url(finalUrl)
                    .build();

            final Call call = NetKit.client().newCall(request);

            response = call.execute();
            networkResponse.httpCode = response.code() + "";

            if (!response.isSuccessful()) {
                networkResponse.setErrorMsg(response.message());
                return networkResponse;
            }


            ResponseBody body = response.body();
            if (body == null) {
                networkResponse.setErrorMsg("response body is null.");
                return networkResponse;
            }

            fos = new FileOutputStream(saveFile, append);
            is = body.byteStream();

            byte[] buf = new byte[DOWNLOAD_BUF];
            float previousPercent = 0;
            long sum = downloadedLength;
            int len;


            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                final float percent = formatPercent(sum, totalLen);

                if (percent - previousPercent < ShareConstants.UPDATE_PROGRESS_GAP) {//只在增量大于总长度一定比例后才通知UI更新，减少UI线程的丢帧率。
                    continue;
                }
                previousPercent = percent;

            }

            fos.flush();

            networkResponse.data = saveFile;

            return networkResponse;

        } catch (final Throwable e) {
            e.printStackTrace();
            networkResponse.setErrorMsg(e.getMessage());
            return networkResponse;

        } finally {
            StreamUtils.closeStreams(is, fos, response);
        }
    }

    private void runAsync(Runnable runnable) {
        NetKit.Internal.runAsync(runnable);
    }

    private static String getNameFromUrl(@NonNull String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    private long getUrlContentLength(String url) {
        final long def = 1;

        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.connect();
            int len = urlConnection.getContentLength();
            return len <= 0 ? def : len;
        } catch (Throwable e) {
            e.printStackTrace();
            return def;
        }
    }


    private static File createFileIfNotExist(String path) throws IOException {
        final File file = new File(path);
        if (file.isDirectory()) {
            file.mkdirs();
        } else if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private static float formatPercent(long currentBytes, long totalBytes) {
        NetworkLog.e(TAG, "currentBytes=" + currentBytes + ",totalBytes=" + totalBytes);

        if (totalBytes == -1) {
            return 0.5F;
        }
        return (float) currentBytes / (float) totalBytes;
    }

    private static void log(String format, Object... args) {
        NetworkLog.e(TAG, String.format(format, args));
    }


    private static class CountingRequestBody extends RequestBody {

        private RequestBody delegate;
        private Listener listener;
        private CountingSink countingSink;

        private CountingRequestBody(RequestBody delegate, Listener listener) {
            this.delegate = delegate;
            this.listener = listener;
        }


        @Override

        public MediaType contentType() {
            return delegate.contentType();
        }


        @Override
        public long contentLength() {
            try {
                return delegate.contentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;

        }


        @Override

        public void writeTo(BufferedSink sink) throws IOException {
            countingSink = new CountingSink(sink);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            delegate.writeTo(bufferedSink);
            bufferedSink.flush();
        }


        private final class CountingSink extends ForwardingSink {

            private long bytesWritten = 0;

            public CountingSink(Sink delegate) {
                super(delegate);
            }


            @Override

            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                listener.onRequestProgress(bytesWritten, contentLength());
            }


        }

        private interface Listener {
            void onRequestProgress(long bytesWritten, long contentLength);
        }


    }


}
