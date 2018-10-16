package com.lch.netkit.v2.filerequest;

import java.util.Map;

/**
 * Created by bbt-team on 2018/2/7.
 */

public class QiNiuParam {

    private String qiniuToken;
    private Map<String, String> uploadOptionParam;
    private String mimeType;
    private boolean checkCrc = false;

    public String getQiniuToken() {
        return qiniuToken;
    }

    public QiNiuParam setQiniuToken(String qiniuToken) {
        this.qiniuToken = qiniuToken;
        return this;
    }

    public Map<String, String> getUploadOptionParam() {
        return uploadOptionParam;
    }

    /**
     * 注意：七牛相关参数名需要以x:开头。
     *
     * @param uploadOptionParam
     * @return
     */
    public QiNiuParam setUploadOptionParam(Map<String, String> uploadOptionParam) {
        this.uploadOptionParam = uploadOptionParam;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public QiNiuParam setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public boolean isCheckCrc() {
        return checkCrc;
    }

    public QiNiuParam setCheckCrc(boolean checkCrc) {
        this.checkCrc = checkCrc;
        return this;
    }

    public static QiNiuParam newInstance() {
        return new QiNiuParam();
    }
}
