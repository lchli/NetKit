package com.lch.netkit.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lch.netkit.imageloader.LiImageLoader;
import com.lch.netkit.v2.NetKit;
import com.lch.netkit.v2.apirequest.ApiRequestParams;
import com.lch.netkit.v2.common.RequestCallback;
import com.lch.netkit.v2.parser.Parser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetKit.setLogEnable(true);
        NetKit.init(this);

        LiImageLoader.instance().init(LiImageLoader.newSetting(this));

        NetKit.apiRequest().asyncGet(new ApiRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"), new Parser<String>() {
            @Override
            public String parse(String responseString) {
                return null;
            }
        }, new RequestCallback<String>() {
            @Override
            public void onSuccess(int httpCode, @Nullable String s) {

            }

            @Override
            public void onError(int httpCode, String msg) {

            }
        });


    }
}
