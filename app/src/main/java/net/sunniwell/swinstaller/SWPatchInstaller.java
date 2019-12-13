package net.sunniwell.swinstaller;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

public class SWPatchInstaller {

    private static final String TAG = "SWPatchInstaller";

    static {
        Log.d(TAG, "class load SWPatchInstaller.");
    }

    public static void loadPatch(Application ctx, List<File> patchPath) {
        PathClassLoader loader = (PathClassLoader) SWPatchInstaller.class.getClassLoader();
        //1:首先获取到当前classloader的PathList
        Field fieldPathList = getField(loader, "pathList");
        try {
            Object loaderPathList = fieldPathList.get(loader);
            //2:然后通过PathLIst获取到当前PathList的Elements
            Field pathList_dexElements = getField(loaderPathList, "dexElements");
            Object[] oldElements = (Object[]) pathList_dexElements.get(loaderPathList);

            //3:生成新的dex的elements
            // list<files> , File optimizeDirectory, List  , Classloader
            Method makeElements = getMethod(loaderPathList, "makeDexElements", List.class, File.class, List.class, ClassLoader.class);
            ArrayList<File> patchFiles = new ArrayList<File>();
            patchFiles.addAll(patchPath);
            File optimizeFolder = ctx.getDir("dexFolder", Context.MODE_PRIVATE);
            ArrayList<IOException> inexceptions = new ArrayList<>();
            Object[] newElements = (Object[]) makeElements.invoke(null, patchFiles, optimizeFolder, inexceptions,loader);
            if (newElements == null || newElements.length == 0) {
                Log.d(TAG, "newElements is empty");
                return;
            }
            Object targetElementArray = Array.newInstance(newElements.getClass().getComponentType(), oldElements.length + newElements.length);
            System.arraycopy(newElements, 0, targetElementArray, 0, newElements.length);
            System.arraycopy(oldElements, 0,
                    targetElementArray, newElements.length, oldElements.length);
            pathList_dexElements.set(loaderPathList, targetElementArray);
            Log.d(TAG, "patchLoaded");

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }


    /**
     * 找到某个object的变量
     *
     * @param instance
     * @param name
     * @return field of this instance
     */
    public static Field getField(Object instance, String name) {
        Class<?> aClass = instance.getClass();
        while (aClass != null) {
            try {
                Field f = aClass.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            aClass = aClass.getSuperclass();

        }

        throw new IllegalArgumentException("sorry, instance:" + instance.getClass().getSimpleName() +
                "has no field:" + name);
    }


    /**
     * 找到某个object的变量
     *
     * @param instance
     * @param name
     * @return field of this instance
     */
    public static Method getMethod(Object instance, String name, Class... parameters) {
        Class<?> aClass = instance.getClass();
        while (aClass != null) {
            try {
                Method m = aClass.getDeclaredMethod(name, parameters);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            aClass = aClass.getSuperclass();

        }

        throw new IllegalArgumentException("sorry, instance:" + instance.getClass().getSimpleName() +
                "has no method:" + name);
    }
}
