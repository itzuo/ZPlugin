package com.zxj.zplugin;

import android.os.Build;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class HookHandler {
    public static String TARGET_INTENT = "target_intent";
    public static void hookAMS(){
        try {
            Field singletonField;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){// 适配8.0以下
                Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
                singletonField = activityManagerNativeClass.getDeclaredField("gDefault");
            }else { //适配8.0、9.0
                //获取Singleton对象
                Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
                //获取activityManager中的IActivityManagerSingleton 字段
                singletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            }
            singletonField.setAccessible(true);
            Object singleton = singletonField.get(null);//获取到获取Singleton对象实例

            //获取系统的IActivityManager对象
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object iActivityManager = mInstanceField.get(singleton);//获取IActivityManager对象

            Class<?> IActivityManagerClass = Class.forName("android.app.IActivityManager");

            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{IActivityManagerClass}, new IActivityManagerProxy(iActivityManager));
            // 使用代理对象替换原有的iActivityManager对象
            mInstanceField.set(singleton,proxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler(){
        try {
            // 获取 ActivityThread 类的 对象
            Class<?>  activityThreadClass = Class.forName("android.app.ActivityThread");
            Field activityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(null);

            // 获取 Handler 对象
            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler) mHField.get(activityThread);

            //设置Callback的值
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);

            //替换系统的callback
            mCallbackField.set(mH,new HCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
