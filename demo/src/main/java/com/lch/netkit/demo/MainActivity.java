package com.lch.netkit.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lch.netkit.NetKit;
import com.lch.netkit.file.helper.NetworkError;
import com.lch.netkit.string.Callback;
import com.lch.netkit.string.Parser;
import com.lch.netkit.string.ResponseValue;
import com.lch.netkit.string.StringRequestParams;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetKit.setLogEnable(true);
        NetKit.init();

        NetKit.stringRequest().get(new StringRequestParams().setUrl("http://api.babytree.com/api/mobile_baby/set_baby_info?pwd=123&name=lich"), new Parser<String>() {
            @Override
            public String parse(String responseString) {
                return null;
            }
        }, new Callback<String>() {
            @Override
            public void onSuccess(String parsedResult) {

            }

            @Override
            public void onFail(String msg) {

            }
        });


        ResponseValue<Object> res = NetKit.stringRequest().getSync(new StringRequestParams(), new Parser<Object>() {
            @Override
            public Object parse(String responseString) {
                return null;
            }
        });

        Object data = res.data;
        NetworkError err = res.err;


    }
}
