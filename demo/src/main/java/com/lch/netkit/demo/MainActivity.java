package com.lch.netkit.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.common.NetworkResponse;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.parser.ModelParser;
import com.lch.netkit.v2.parser.Parser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetKit.setLogEnable(true);
        NetKit.init(this);


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
