package com.lch.netkit.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.filerequest.DownloadFileCallback;
import com.lch.netkit.v2.filerequest.FileOptions;
import com.lch.netkit.v2.filerequest.UploadFileCallback;
import com.lch.netkit.v2.okinterceptor.ApiInterceptor;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetKit.setLogEnable(true);
        NetKit.init(this);
        NetKit.setApiInterceptor(new ApiInterceptor() {
            @NonNull
            @Override
            public ApiRequestParams interceptApiRequestParams(@NonNull ApiRequestParams requestParams) {
                requestParams.addParam("token", "token");
                requestParams.addParam("uid", "uid");
                requestParams.addParam("sign", genarateSign(requestParams.params()));
                return requestParams;
            }

            @Override
            public String interceptResponse(String responseString) {
                try {
                    JSONObject res = new JSONObject(responseString);
                    if (res.optInt("code") == 10002) {
                        Toast.makeText(getApplicationContext(), "token无效，跳转登录页面", Toast.LENGTH_LONG).show();
                        Intent it = new Intent("login");
                        startActivity(it);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return responseString;
            }
        });


    }

    private void asyncGet() {
        NetKit.apiRequest().asyncGet(new ApiRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"),
                User.class, new RequestCallback<User>() {
                    @Override
                    public void onSuccess(String httpCode, @Nullable User user) {

                    }

                    @Override
                    public void onError(String httpCode, String msg) {

                    }
                });
    }

    private void syncGet() {
        NetworkResponse<User> resp = NetKit.apiRequest().syncGet(new ApiRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"),
                User.class);
    }

    private void syncPost() {
        NetworkResponse<User> resp = NetKit.apiRequest().syncPost(new ApiRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"),
                User.class);
    }

    private void uploadFile() {
        ApiRequestParams params = new ApiRequestParams()
                .setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich")
                .addParam("uid", "userId")
                .addFile(new FileOptions().setFilePath("/filepath/").setFileKey("files"))
                .addFile(new FileOptions().setFilePath("/filepath2/").setFileKey("files"));

        NetKit.fileRequest().uploadFile(params, User.class, new UploadFileCallback<User>() {
            @Override
            public void onProgress(double percent) {

            }

            @Override
            public void onSuccess(String httpCode, @Nullable User user) {

            }

            @Override
            public void onError(String httpCode, String msg) {

            }
        });
    }

    private void syncUploadFile() {
        ApiRequestParams params = new ApiRequestParams()
                .setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich")
                .addParam("uid", "userId")
                .addFile(new FileOptions().setFilePath("/filepath/").setFileKey("files"))
                .addFile(new FileOptions().setFilePath("/filepath2/").setFileKey("files"));

        NetworkResponse<User> reponse = NetKit.fileRequest().syncUploadFile(params, User.class);
    }

    private void downloadFile() {
        ApiRequestParams params = new ApiRequestParams()
                .setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich")
                .setDownloadFileSavePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "test.jpg")
                .addParam("uid", "userId");
        NetKit.fileRequest().downloadFile(params, new DownloadFileCallback() {
            @Override
            public void onProgress(double percent) {

            }

            @Override
            public void onSuccess(String httpCode, @Nullable File file) {

            }

            @Override
            public void onError(String httpCode, String msg) {

            }
        });
    }


    private String genarateSign(Map<String, String> params) {
        return "test";
    }

    static class User {

    }
}
