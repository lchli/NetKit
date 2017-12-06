package com.lch.netkit.common.base;

import android.support.v7.app.AppCompatActivity;

import com.lch.netkit.common.tool.VF;

/**
 * Created by bbt-team on 2017/12/6.
 */

public class BaseCompatActivity extends AppCompatActivity {

    public <T> T f(int id) {
        return VF.f(this, id);
    }
}
