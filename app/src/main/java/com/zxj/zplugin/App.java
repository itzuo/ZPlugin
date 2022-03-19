package com.zxj.zplugin;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoadUtil.loadClass(this);
        HookHandler.hookAMS();
        HookHandler.hookHandler();
    }
}
