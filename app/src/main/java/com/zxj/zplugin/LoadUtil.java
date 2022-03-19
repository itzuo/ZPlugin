package com.zxj.zplugin;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class LoadUtil {

//    private static String apkPath = "/sdcard/test.dex";
    private static String apkPath = "/sdcard/plugin-debug.apk";

    public static void loadClass(Context context){
        // 获取 pathList 的字段
        try {
            Class baseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = baseDexClassLoaderClass.getDeclaredField("pathList");
            pathListField.setAccessible(true);

            // 获取 dexElements 字段
            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            /**
             * 获取插件的 dexElements[]
             */
            // 获取 DexClassLoader 类中的属性 pathList 的值
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath,
                    context.getCacheDir().getAbsolutePath(),
                    null,context.getClassLoader());
            Object pluginPathList = pathListField.get(dexClassLoader);//插件的DexPathList对象
            // 获取 pathList 中的属性 dexElements[] 的值--- 插件的 dexElements[]
            Object[] pluginDexElements = (Object[])dexElementsField.get(pluginPathList);

            /**
             * 获取宿主的 dexElements[]
             */
            // 获取 PathClassLoader 类中的属性 pathList 的值
            PathClassLoader pathClassLoader = (PathClassLoader)context.getClassLoader();
            Object hostPathList = pathListField.get(pathClassLoader);
            // 获取 pathList 中的属性 dexElements[] 的值--- 宿主的 dexElements[]
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

            /**
             * 将插件的 dexElements[] 和宿主的 dexElements[] 合并为一个新的 dexElements[]
             */
            // 创建一个新的空数组，第一个参数是数组的类型，第二个参数是数组的长度
            Object[] newDexElements = (Object[]) Array.newInstance(
                    hostDexElements.getClass().getComponentType(),
                    hostDexElements.length + pluginDexElements.length);
            System.arraycopy(hostDexElements,0,newDexElements,0,hostDexElements.length);
            System.arraycopy(pluginDexElements,0,newDexElements,hostDexElements.length,pluginDexElements.length);

            /**
             * 将生成的新值赋给 "dexElements" 属性
             */
            dexElementsField.set(hostPathList,newDexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
