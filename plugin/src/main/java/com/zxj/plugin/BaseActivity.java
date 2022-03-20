package com.zxj.plugin;

import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    public Resources getResources() {
        if (getApplication() != null && getApplication().getResources() != null) {
            // 因为宿主重写了该方法，所以获取的将是新创建的 resources 对象
            return getApplication().getResources();
        }
        return super.getResources();
    }
}
