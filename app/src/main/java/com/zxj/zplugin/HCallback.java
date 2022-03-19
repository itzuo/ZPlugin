package com.zxj.zplugin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

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
                    proxyIntent.setComponent(intent.getComponent());
//                intentField.set(r,intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EXECUTE_TRANSACTION:// 适配9.0(API28)

                break;
        }
        return false;
    }
}
