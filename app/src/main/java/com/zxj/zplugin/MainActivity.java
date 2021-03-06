package com.zxj.zplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printClassLoader();
    }

    private void printClassLoader(){
        ClassLoader classLoader = getClassLoader();
        while (classLoader != null) {
            Log.e("zxj", "classLoader:" + classLoader);
            classLoader = classLoader.getParent();
        }
        Log.e("zxj", "Activity->classLoader:" + Activity.class.getClassLoader());
    }

    public void onStartPlugin(View view) {
        /*DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/test.dex",
                getCacheDir().getAbsolutePath(),null,getClassLoader());*/
        try {
//            Class<?> clazz = dexClassLoader.loadClass("com.zxj.plugin.Test");
            Class<?> clazz = Class.forName("com.zxj.plugin.Test");
            Method method = clazz.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 找到一个容易替换Intent的地方
//      startActivity(new Intent(MainActivity.this,ProxyActivity.class));
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.zxj.plugin",
                "com.zxj.plugin.PluginActivity"));
        startActivity(intent);
    }
}