package com.zxj.zplugin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.List;

public class HCallback implements Handler.Callback{
    public static final int LAUNCH_ACTIVITY = 100 ;
    public static final int EXECUTE_TRANSACTION = 159;
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what){
            case LAUNCH_ACTIVITY:
                Object r = msg.obj;//ActivityClientRecord
                try {
                    //得到消息中的Intent（启动ProxyActivity的Intent)
                    Field intentField = r.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent proxyIntent = (Intent) intentField.get(r);

                    // 得到此前保存起来的Intent （启动插件Activity的Intent)
                    Intent intent = proxyIntent.getParcelableExtra(HookHandler.TARGET_INTENT);
                    //将启动ProxyActivity的Intent替换为启动插件Activity的Intent
//                    proxyIntent.setComponent(intent.getComponent());
                    if(intent != null) {
                        intentField.set(r, intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EXECUTE_TRANSACTION:// 适配9.0(API28)
                try {
                    // 获取 mActivityCallbacks 对象
                    Field mActivityCallbacksField = msg.obj.getClass().getDeclaredField("mActivityCallbacks");
                    mActivityCallbacksField.setAccessible(true);
                    List mActivityCallbacks = (List) mActivityCallbacksField.get(msg.obj);

                    for (int i = 0; i < mActivityCallbacks.size(); i++) {
                        if (mActivityCallbacks.get(i).getClass().getName()
                                .equals("android.app.servertransaction.LaunchActivityItem")) {
                            Object launchActivityItem = mActivityCallbacks.get(i);

                            // 获取启动代理的 Intent
                            Field mIntentField = launchActivityItem.getClass().getDeclaredField("mIntent");
                            mIntentField.setAccessible(true);
                            Intent proxyIntent = (Intent) mIntentField.get(launchActivityItem);

                            // 目标 intent 替换 proxyIntent
                            Intent intent = proxyIntent.getParcelableExtra(HookHandler.TARGET_INTENT);
                            if (intent != null) {
                                mIntentField.set(launchActivityItem, intent);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }
}
