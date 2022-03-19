package com.zxj.zplugin;

import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IActivityManagerProxy implements InvocationHandler {

    private Object mActivityManager;
    public IActivityManagerProxy(Object activityManager){
        this.mActivityManager = activityManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 当执行的方法是 startActivity 时作处理
        if("startActivity".equals(method.getName())){
            // 获取 Intent 参数在 args 数组中的index值
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if(args[i] instanceof Intent){
                    index = i;
                    break;
                }
            }

            // 得到原始的 Intent 对象
            Intent intent = (Intent) args[index];

            // 生成代理proxyIntent
            Intent proxyIntent = new Intent();
            proxyIntent.setClassName("com.zxj.zplugin",ProxyActivity.class.getName());
            //保存原始的Intent对象，便于以后还原。
            proxyIntent.putExtra(HookHandler.TARGET_INTENT,intent);
            // 使用proxyIntent替换数组中的Intent
            args[index] = proxyIntent;
        }
        return method.invoke(mActivityManager,args);
    }
}
