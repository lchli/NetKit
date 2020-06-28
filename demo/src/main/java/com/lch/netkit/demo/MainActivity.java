package com.lch.netkit.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.filerequest.DownloadFileParams;
import com.lch.netkit.v2.filerequest.UploadFileParams;
import com.lch.netkit.v2.okinterceptor.ApiInterceptor;

import org.json.JSONObject;

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
                return null;
            }

            @NonNull
            @Override
            public UploadFileParams interceptUploadFileParams(@NonNull UploadFileParams requestParams) {
                return null;
            }

            @NonNull
            @Override
            public DownloadFileParams interceptDownloadFileParams(@NonNull DownloadFileParams requestParams) {
                return null;
            }

            @Override
            public String interceptResponse(String responseString) {
                try {
                    JSONObject res=  new JSONObject(responseString);
                    if(res.optInt("code")==10002){
                        Toast.makeText(getApplicationContext(),"token无效，跳转登录页面",Toast.LENGTH_LONG).show();
                        Intent it=new Intent("login");
                        startActivity(it);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return responseString;
            }
        });


        NetKit.apiRequest().asyncGet(new ApiRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"),
               User.class, new RequestCallback<User>() {
                    @Override
                    public void onSuccess(String httpCode, @Nullable User user) {

                    }

                    @Override
                    public void onError(String httpCode, String msg) {

                    }
                });


        NetworkResponse<User> resp = NetKit.apiRequest().syncGet(new ApiRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"),
                User.class);



    }

    static class User{

    }
}
