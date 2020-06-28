package com.lch.netkit.v2.apirequest;


import androidx.annotation.NonNull;

import com.lch.netkit.v2.common.CommonParams;
import com.lch.netkit.v2.filerequest.FileOptions;
import com.lch.netkit.v2.filerequest.QiNiuParam;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;

/**
 * Created by lichenghang.
 */

public class ApiRequestParams extends CommonParams<ApiRequestParams> {

    private RequestBody requestBody;


    public ApiRequestParams setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return thisObject();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    @Override
    protected ApiRequestParams thisObject() {
        return this;
    }

    private String downloadFileSavePath;


    public String getDownloadFileSavePath() {
        return downloadFileSavePath;
    }

    public ApiRequestParams setDownloadFileSavePath(String saveDir) {
        this.downloadFileSavePath = saveDir;
        return thisObject();
    }


    private QiNiuParam mQiNiuParam = QiNiuParam.newInstance();
    private final List<FileOptions> fileOptionList = new ArrayList<>(1);
    private ApiRequestParams.ServerType serverType = ApiRequestParams.ServerType.URL_DEFINED;

    public enum ServerType {
        QI_NIU,
        URL_DEFINED
    }


    public ApiRequestParams.ServerType getServerType() {
        return serverType;
    }

    /**
     * @param serverType {@link ApiRequestParams.ServerType#QI_NIU},def is {@link ApiRequestParams.ServerType#URL_DEFINED}
     * @return
     */
    public ApiRequestParams setServerType(ApiRequestParams.ServerType serverType) {
        this.serverType = serverType;
        return thisObject();
    }

    public ApiRequestParams addFile(FileOptions fileOptions) {
        fileOptionList.add(fileOptions);
        return thisObject();
    }

    public List<FileOptions> files() {
        return fileOptionList;
    }


    public ApiRequestParams setQiNiuParams(QiNiuParam params) {
        if (params != null) {
            mQiNiuParam = params;
        }
        return thisObject();
    }

    @NonNull
    public QiNiuParam getQiNiuParam() {
        return mQiNiuParam;
    }



}
