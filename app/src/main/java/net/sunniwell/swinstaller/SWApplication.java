package net.sunniwell.swinstaller;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class SWApplication extends Application {
    public static final String TAG = "SWApplication";

    @Override
    public void onCreate() {
        Log.d(TAG,"createApplication");
        super.onCreate();
        Log.d(TAG, "onApplication create currentClassLoader:" + SWApplication.class.getClassLoader());
        File cacheFile = getApplicationContext().getCacheDir();
        File data = getApplicationContext().getDataDir();
        Log.d(TAG,"cache:" + cacheFile.getAbsolutePath()+";data:" + data.getAbsolutePath());
        List<File> patchFiles = getPatchFile(cacheFile);
        SWPatchInstaller.loadPatch(SWApplication.this,patchFiles);

      /*  File cacheFile = getApplicationContext().getCacheDir();
        File data = getApplicationContext().getDataDir();
        Log.d(TAG,"cache:" + cacheFile.getAbsolutePath()+";data:" + data.getAbsolutePath());
        List<File> patchFiles = getPatchFile(cacheFile);


        PathClassLoader loader = (PathClassLoader) SWApplication.class.getClassLoader();
        try {
             if(patchFiles !=null && patchFiles.size()>0){
           Class<? super PathClassLoader> baseDexClassLoader = PathClassLoader.class.getSuperclass();
            Field pathList = baseDexClassLoader.getDeclaredField("pathList");
            pathList.setAccessible(true);
            Object dexPathList = pathList.get(loader);// 当前object是DexpathList

            Class DexPathListClass = dexPathList.getClass();
            Field ElementField = DexPathListClass.getDeclaredField("dexElements");
            ElementField.setAccessible(true);
            Object ElementArray = ElementField.get(dexPathList);
            Object[] elements = (Object[]) ElementArray;// DexPathList中的Elements数组
            Log.d(TAG,"original Element size:" + elements.length);
            Class<?> Element = elements[0].getClass();



           // DexFile file = new DexFile(patchFiles.get(0).getAbsolutePath());
            File dexZipPath;

//            Constructor<?> constructor = Element.getConstructor(new Class[]{DexFile.class});
//            Object o = constructor.newInstance(file);// new Element
                 Object o = getDexElement(patchFiles.get(0).getAbsolutePath())[0];
            Object array = Array.newInstance(Element,elements.length+1);
            Array.set(array,0,o);
            int Length = elements.length+1;
            for (int i = 1; i < Length; i++) {
                Array.set(array,i,elements[i-1]);
            }
            elements = (Object[]) array;
            Log.d(TAG,"array.length:" +((Object[]) array).length);
             }else{
                 Log.d(TAG,"PatchList is empty");
             }
        } catch (NoSuchFieldException | IllegalAccessException   e) {
            e.printStackTrace();
        }*/
    }

    /**
     * dex file path
     * @param path
     * @return
     */
    public Object[] getDexElement(String path){
        // Path 是当前patch包dex文件地址
        try { PathClassLoader loader = (PathClassLoader) SWApplication.class.getClassLoader();
        PathClassLoader pathClassLoader = new PathClassLoader(path,loader);
        Class<?> superclass = pathClassLoader.getClass().getSuperclass();
        Field pathList = null;
        pathList = superclass.getDeclaredField("pathList");
        pathList.setAccessible(true);
        Object dexPathList = pathList.get(loader);// 当前object是DexpathList

        Class DexPathListClass = dexPathList.getClass();
        Field ElementField = DexPathListClass.getDeclaredField("dexElements");
        ElementField.setAccessible(true);
        Object ElementArray = ElementField.get(dexPathList);
        Object[] elements = (Object[]) ElementArray;// DexPathList中的Elements数组
            Log.d(TAG,"Elemtn:" + elements[0]);
            Log.d(TAG,"size:" + elements.length);
            return elements;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<File> getPatchFile(File parentFolder) {
        Log.d(TAG,"parentFolder:" + parentFolder.getAbsolutePath());
        Log.d(TAG,"exits:" + parentFolder.exists());
        ArrayList<File> patchFiles = new ArrayList<>();
        if (parentFolder != null && parentFolder.exists()) {
            Log.d(TAG, "parentFolder exist:" + parentFolder.getAbsolutePath());
            File[] patchFile = parentFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    Log.d(TAG,"name:" + name);
                    if (name.endsWith("swpatch")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File f : patchFile
            ) {
                patchFiles.add(f);

            }
        }
        ;
        return patchFiles;
    }


}
