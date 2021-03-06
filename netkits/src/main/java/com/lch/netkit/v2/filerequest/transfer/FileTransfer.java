package com.lch.netkit.v2.filerequest.transfer;
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
import android.support.annotation.Nullable;


import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.common.Cancelable;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.filerequest.DownloadFileCallback;
import com.lch.netkit.v2.filerequest.UploadFileCallback;
import com.lch.netkit.v2.parser.Parser;

import java.io.File;


public interface FileTransfer {


    @Nullable
    <T> Cancelable uploadFile(ApiRequestParams fileParams, final Parser<T> parser, final UploadFileCallback<T> listener);


    @NonNull
    <T> NetworkResponse<T> syncUploadFile(ApiRequestParams fileParams, final Parser<T> parser);

    @Nullable
    Cancelable downloadFile(ApiRequestParams fileParams, final DownloadFileCallback listener);


    @NonNull
    NetworkResponse<File> syncDownloadFile(ApiRequestParams fileParams);



}
