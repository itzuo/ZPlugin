package com.zxj.plugin;

import android.os.Bundle;
import android.util.Log;

public class PluginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        Log.e("zxj","onCreate：启动PluginActivity");
    }
}