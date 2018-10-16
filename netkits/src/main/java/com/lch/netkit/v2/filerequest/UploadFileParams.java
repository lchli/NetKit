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

import android.support.annotation.NonNull;

import com.lch.netkit.v2.common.CommonParams;

import java.util.ArrayList;
import java.util.List;


public class UploadFileParams extends CommonParams<UploadFileParams> {

    private QiNiuParam mQiNiuParam = QiNiuParam.newInstance();
    private final List<FileOptions> fileOptionList = new ArrayList<>(1);
    private ServerType serverType = ServerType.URL_DEFINED;

    public enum ServerType {
        QI_NIU,
        URL_DEFINED
    }


    private UploadFileParams() {
    }

    @Override
    protected UploadFileParams thisObject() {
        return this;
    }

    public ServerType getServerType() {
        return serverType;
    }

    /**
     * @param serverType {@link ServerType#QI_NIU},def is {@link ServerType#URL_DEFINED}
     * @return
     */
    public UploadFileParams setServerType(ServerType serverType) {
        this.serverType = serverType;
        return thisObject();
    }

    public UploadFileParams addFile(FileOptions fileOptions) {
        fileOptionList.add(fileOptions);
        return thisObject();
    }

    public List<FileOptions> files() {
        return fileOptionList;
    }


    public UploadFileParams setQiNiuParams(QiNiuParam params) {
        if (params != null) {
            mQiNiuParam = params;
        }
        return thisObject();
    }

    @NonNull
    public QiNiuParam getQiNiuParam() {
        return mQiNiuParam;
    }

    public static UploadFileParams newInstance() {
        return new UploadFileParams();
    }


}
