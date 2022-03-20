package com.zxj.zplugin;

import android.app.Application;
import android.content.res.Resources;

public class App extends Application {
    private Resources resources;
    @Override
    public void onCreate() {
        super.onCreate();
        LoadUtil.loadClass(this);
        // 获取新建的resources资源
        resources = LoadUtil.loadResource(this);
        HookHandler.hookAMS();
        HookHandler.hookHandler();
    }

    // 重写该方法，当resources为空时，相当于没有重写，不为空时，返回新建的resources对象
    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }
}
