package com.lch.netkit.file.transfer.impl;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.EncryptUtils;
import com.lch.netkit.NetKit;
import com.lch.netkit.common.mvc.ResponseValue;
import com.lch.netkit.file.helper.DownloadFileParams;
import com.lch.netkit.file.helper.FileConst;
import com.lch.netkit.file.helper.FileOptions;
import com.lch.netkit.file.helper.FileTransferCallback;
import com.lch.netkit.file.helper.FileTransferState;
import com.lch.netkit.file.helper.UploadFileParams;
import com.lch.netkit.file.transfer.FileTransfer;
import com.lch.netkit.string.Parser;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 上传地址由url指定的文件传输器实现。
 */
public class FileTransferImpl extends FileTransfer {

    private static final String TAG = "FileTransferImpl";
    private static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private static final int DOWNLOAD_BUF = 1024;

    private final ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));

    private final OkHttpClient mOkHttpClient = NetKit.client();


    @Override
    public <T> String uploadFile(final UploadFileParams fileParams, @NonNull final Parser<T> parser, @NonNull final FileTransferCallback<T> listener) {

        final ResponseValue<T> res = new ResponseValue<>();

        if (fileParams == null) {
            res.setErrMsg("fileParams is null.");
            NetKit.runInUI(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(res);
                }
            });

            return null;
        }
        if (TextUtils.isEmpty(fileParams.getUrl())) {
            res.setErrMsg("file url is empty.");
            NetKit.runInUI(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(res);
                }
            });

            return null;
        }


        final Iterator<FileOptions> filesIter = fileParams.files();

        final String requestID = UUID.randomUUID().toString();

        final FileTransferState state = new FileTransferState();
        calls.put(requestID, state);

        runAsync(new Runnable() {
            @Override
            public void run() {

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                Iterator<Map.Entry<String, String>> textParamsIter = fileParams.textParams();
                while (textParamsIter.hasNext()) {
                    Map.Entry<String, String> item = textParamsIter.next();
                    builder.addFormDataPart(item.getKey(), item.getValue());
                }

                while (filesIter.hasNext()) {
                    FileOptions fileOpt = filesIter.next();
                    if (fileOpt.getFile() != null) {
                        builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFile()));
                    } else if (fileOpt.getFilePath() != null) {
                        builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, new File(fileOpt.getFilePath())));
                    } else if (fileOpt.getFileBytes() != null) {
                        builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFileBytes()));
                    }
                }

                Request.Builder requestBuilder = new Request.Builder();

                Iterator<Map.Entry<String, String>> headers = fileParams.headers();
                while (headers.hasNext()) {
                    Map.Entry<String, String> header = headers.next();
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }

                RequestBody requestBody = builder.build();

                Request request = requestBuilder
                        .url(fileParams.getUrl())
                        .post(new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
                            float previousPercent = 0;

                            @Override
                            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                                final float percent = formatPercent(bytesWritten, contentLength);
                                state.setProgressPercent(percent);

                                if (bytesWritten < contentLength && (percent - previousPercent) < FileConst.UPDATE_PROGRESS_GAP) {
                                    return;
                                }
                                previousPercent = percent;

                                NetKit.runInUI(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onProgress(percent);
                                    }
                                });

                            }
                        }))
                        .build();

                final Call call = mOkHttpClient.newCall(request);
                state.setCall(call);

                try {
                    if (state.isCanceled()) {
                        res.setErrMsg("canceled");
                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(res);
                            }
                        });

                        return;
                    }

                    final Response response = call.execute();
                    if (!response.isSuccessful()) {
                        res.setErrMsg(response.code(), response.message());
                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(res);
                            }
                        });

                        return;
                    }
                    ResponseBody body = response.body();
                    if (body == null) {
                        res.setErrMsg("response body is null");
                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(res);
                            }
                        });

                        return;
                    }

                    res.data = parser.parse(body.string());

                    NetKit.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            listener.onComplete(res);
                        }
                    });


                } catch (final Throwable e) {
                    e.printStackTrace();
                    res.setErrMsg(e.getMessage());
                    NetKit.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            listener.onComplete(res);
                        }
                    });

                } finally {
                    calls.remove(requestID);
                }

            }
        });

        return requestID;
    }


    @NonNull
    @Override
    public <T> ResponseValue<T> uploadFileSync(final UploadFileParams fileParams, @NonNull final Parser<T> parser) {

        ResponseValue<T> res = new ResponseValue<>();

        try {

            if (fileParams == null) {
                res.setErrMsg("fileParams is null.");
                return res;
            }

            final Iterator<FileOptions> filesIter = fileParams.files();

            if (TextUtils.isEmpty(fileParams.getUrl())) {
                res.setErrMsg("uploadUrl is empty.");
                return res;
            }

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            Iterator<Map.Entry<String, String>> textParamsIter = fileParams.textParams();
            while (textParamsIter.hasNext()) {
                Map.Entry<String, String> item = textParamsIter.next();
                builder.addFormDataPart(item.getKey(), item.getValue());
            }

            while (filesIter.hasNext()) {
                FileOptions fileOpt = filesIter.next();
                if (fileOpt.getFile() != null) {
                    builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFile()));
                } else if (fileOpt.getFilePath() != null) {
                    builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, new File(fileOpt.getFilePath())));
                } else if (fileOpt.getFileBytes() != null) {
                    builder.addFormDataPart(fileOpt.getFileKey(), fileOpt.getFileName(), RequestBody.create(MEDIA_TYPE_STREAM, fileOpt.getFileBytes()));
                }
            }

            Request.Builder requestBuilder = new Request.Builder();

            Iterator<Map.Entry<String, String>> headers = fileParams.headers();
            while (headers.hasNext()) {
                Map.Entry<String, String> header = headers.next();
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }

            RequestBody requestBody = builder.build();

            Request request = requestBuilder
                    .url(fileParams.getUrl())
                    .post(requestBody)
                    .build();

            final Call call = mOkHttpClient.newCall(request);


            final Response response = call.execute();
            if (!response.isSuccessful()) {
                res.setErrMsg(response.code(), response.message());
                return res;
            }

            ResponseBody body = response.body();
            if (body == null) {
                res.setErrMsg(response.code(), "response body is null");
                return res;
            }

            res.data = parser.parse(body.string());

            return res;

        } catch (final Throwable e) {
            e.printStackTrace();
            res.setErrMsg(e.getMessage());
            return res;
        }

    }

    @Override
    public String downloadFile(final DownloadFileParams fileParams, @NonNull final FileTransferCallback<File> listener) {
        final ResponseValue<File> res = new ResponseValue<>();

        if (fileParams == null) {
            res.setErrMsg("fileParams is null.");
            NetKit.runInUI(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(res);
                }
            });

            return null;
        }
        if (TextUtils.isEmpty(fileParams.getUrl())) {
            res.setErrMsg("file url is empty.");
            NetKit.runInUI(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(res);
                }
            });

            return null;
        }
        if (TextUtils.isEmpty(fileParams.getSaveDir())) {
            res.setErrMsg("file save dir is invalid.");
            NetKit.runInUI(new Runnable() {
                @Override
                public void run() {
                    listener.onComplete(res);
                }
            });

            return null;
        }
        final String requestID = EncryptUtils.encryptMD5ToString(fileParams.getUrl());

        final FileTransferState state = new FileTransferState();
        calls.put(requestID, state);

        runAsync(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                FileOutputStream fos = null;

                try {

                    Request.Builder requestBuilder = new Request.Builder();

                    Iterator<Map.Entry<String, String>> headers = fileParams.headers();
                    while (headers.hasNext()) {
                        Map.Entry<String, String> header = headers.next();
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }


                    long totalLen = getUrlContentLength(fileParams.getUrl());
                    log("totalLen=%d", totalLen);

                    final String saveFileName = EncryptUtils.encryptMD5ToString(fileParams.getUrl()) + getNameFromUrl(fileParams.getUrl());
                    createFileIfNotExist(fileParams.getSaveDir());
                    final File saveFile = createFileIfNotExist(fileParams.getSaveDir() + "/" + saveFileName);
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
                            .url(fileParams.getUrl())
                            .build();
                    final Call call = mOkHttpClient.newCall(request);
                    state.setCall(call);

                    if (state.isCanceled()) {
                        res.setErrMsg("canceled");
                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(res);
                            }
                        });

                        return;
                    }

                    final Response response = call.execute();

                    if (!response.isSuccessful()) {
                        res.setErrMsg(response.code(), response.message());
                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(res);
                            }
                        });

                        return;
                    }

                    ResponseBody body = response.body();
                    if (body == null) {
                        res.setErrMsg(response.code(), "response body is null");
                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onComplete(res);
                            }
                        });

                        return;
                    }

                    fos = new FileOutputStream(saveFile, append);
                    is = body.byteStream();

                    byte[] buf = new byte[DOWNLOAD_BUF];
                    float previousPercent = 0;
                    long sum = downloadedLength;
                    int len;

                    sendProgress(sum, totalLen, state, listener);

                    while ((len = is.read(buf)) != -1) {
                        sum += len;
                        fos.write(buf, 0, len);

                        final float percent = formatPercent(sum, totalLen);
                        state.setProgressPercent(percent);

                        if (percent - previousPercent < FileConst.UPDATE_PROGRESS_GAP) {
                            continue;
                        }
                        previousPercent = percent;

                        NetKit.runInUI(new Runnable() {
                            @Override
                            public void run() {
                                listener.onProgress(percent);
                            }
                        });

                    }

                    sendProgress(sum, totalLen, state, listener);

                    fos.flush();

                    res.data = saveFile;

                    NetKit.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            listener.onComplete(res);
                        }
                    });


                } catch (final Exception e) {
                    e.printStackTrace();
                    res.setErrMsg(e.getMessage());

                    NetKit.runInUI(new Runnable() {
                        @Override
                        public void run() {
                            listener.onComplete(res);
                        }
                    });

                } finally {
                    IOUtils.closeQuietly(is, fos);
                    calls.remove(requestID);
                }
            }
        });

        return requestID;

    }


    @NonNull
    @Override
    public ResponseValue<File> downloadFileSync(final DownloadFileParams fileParams) {

        ResponseValue<File> res = new ResponseValue<>();
        InputStream is = null;
        FileOutputStream fos = null;

        try {

            if (fileParams == null) {
                res.setErrMsg("fileParams is null.");
                return res;
            }

            if (TextUtils.isEmpty(fileParams.getUrl())) {
                res.setErrMsg("file url is empty.");
                return res;
            }

            if (TextUtils.isEmpty(fileParams.getSaveDir())) {
                res.setErrMsg("file save dir is invalid.");
                return res;
            }

            Request.Builder requestBuilder = new Request.Builder();

            Iterator<Map.Entry<String, String>> headers = fileParams.headers();
            while (headers.hasNext()) {
                Map.Entry<String, String> header = headers.next();
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }

            long totalLen = getUrlContentLength(fileParams.getUrl());
            log("totalLen=%d", totalLen);

            final String saveFileName = EncryptUtils.encryptMD5ToString(fileParams.getUrl()) + getNameFromUrl(fileParams.getUrl());
            createFileIfNotExist(fileParams.getSaveDir());
            final File saveFile = createFileIfNotExist(fileParams.getSaveDir() + "/" + saveFileName);
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
                    .url(fileParams.getUrl())
                    .build();
            final Call call = mOkHttpClient.newCall(request);

            final Response response = call.execute();

            if (!response.isSuccessful()) {
                res.setErrMsg(response.code(), response.message());
                return res;
            }

            ResponseBody body = response.body();
            if (body == null) {
                res.setErrMsg("response body is null");
                return res;
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

                float percent = formatPercent(sum, totalLen);

                if (percent - previousPercent < FileConst.UPDATE_PROGRESS_GAP) {
                    continue;
                }
                previousPercent = percent;

            }

            fos.flush();

            res.data = saveFile;

            return res;

        } catch (final Throwable e) {
            e.printStackTrace();
            res.setErrMsg(e.getMessage());
            return res;

        } finally {
            IOUtils.closeQuietly(is, fos);
        }

    }

    private void runAsync(Runnable runnable) {
        executorService.execute(runnable);
    }

    private void sendProgress(long currentBytes, long totalBytes, FileTransferState state, final FileTransferCallback<File> listener) {
        final float percent = formatPercent(currentBytes, totalBytes);
        state.setProgressPercent(percent);

        NetKit.runInUI(new Runnable() {
            @Override
            public void run() {
                listener.onProgress(percent);
            }
        });
    }

    private static String getNameFromUrl(@NonNull String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    private long getUrlContentLength(String url) throws Exception {
        Request request = new Request.Builder()
                .head()
                .url(url)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        ResponseBody body = response.body();
        if (body == null) {
            throw new IOException("can not get Content-Length url=" + url);
        }
        long len = body.contentLength();
        body.close();
        return len;
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
        if (totalBytes == -1) {
            return 0.5F;
        }
        return (float) currentBytes / (float) totalBytes;
    }

    private static void log(String format, Object... args) {
        log(TAG, String.format(format, args));
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
