package com.lch.netkit.v2.util;

public final class ShareConstants {

    public static final String HTTP_HEADER_GZIP_POLICY = "baf_http_header_gzip_policy";
    public static final String HTTP_HEADER_GZIP_NO = "0";
    public static final String HTTP_HEADER_GZIP_YES = "1";

    public static final int HTTP_ERR_CODE_UNKNOWN = -99999;

    /*每当进度增加下载文件的10分之1时更新一下进度，避免频繁更新向UI线程发送过多Runnable阻塞UI线程。*/
    public static final float UPDATE_PROGRESS_GAP = 0.01F;
}
