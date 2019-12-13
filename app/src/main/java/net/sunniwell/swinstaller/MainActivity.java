package net.sunniwell.swinstaller;


import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.sunniwell.swinstaller.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_MOVIES;

public class MainActivity extends AppCompatActivity {
    //private ResultProfileBinding binding;
    private ActivityMainBinding binding ;
    public static final String TAG = "MainActivity";
    public static final String ACTION_UnINSTALL_COMPLETE = "net.sunniwell.actionUNInstallDone";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"broadcast:"+intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG,"Application net.sunniwell.swinstaller.SWUtil:" + SWUtil.getCurrentIndex());
        binding.mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onButtonClicked");
//                String packagePath = "mnt/sdcard/qqyy_5.9.0.22_dangbei.apk";
//                File file = new File(packagePath);
//                Log.d(TAG,"package exist:" + file.exists());
//                new PackageInstallerT(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,packagePath);

                    new CreateFile("createFile").start();


            }
        });

        binding.mainBtnUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onButtonClickedUninstall");
                //uninstall();
//                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"checkSelfPermission");
                    ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                }else{
//                    new CreateFile("createFile").start();
//                }

            }
        });

        initReceiver();

    }
    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("net.sunniwell.actionInstallDone");
        filter.addAction(ACTION_UnINSTALL_COMPLETE);
        MainActivity.this.registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mReceiver !=null){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG,"onRequestPermissionsResult granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    new CreateFile("createFile").start();


                } else {
                    Log.i(TAG,"onRequestPermissionsResult denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showWaringDialog();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showWaringDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        finish();
                    }
                }).show();
    }


    private void uninstall(){
        String appPackage = "com.tencent.qqmusictv";
        Intent intent = new Intent( ACTION_UnINSTALL_COMPLETE);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        PackageInstaller mPackageInstaller = MainActivity.this.getPackageManager().getPackageInstaller();
        mPackageInstaller.uninstall(appPackage, sender.getIntentSender());
    }


    private class CreateFile  extends  Thread{

        public CreateFile(String name){
            super(name);
        }

        @Override
        public void run() {
             Log.d(TAG,"create new file start ");
             File file =  new File("/storage/F27F-5AEB");
             if(file.exists()){
                 Log.d(TAG,"create new file exists ");
                 File auto = new File(file,"zuto.txt") ;
                 try {
                     auto.createNewFile();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }

          File environmentFile =   Environment.getExternalStorageDirectory();
            Log.d(TAG,"environmentFile" + environmentFile.getAbsolutePath());
            File movie =   Environment.getExternalStoragePublicDirectory( DIRECTORY_MOVIES);
            Log.d(TAG,"movie" + movie.getAbsolutePath());

            File[] extFile  = MainActivity.this.getExternalFilesDirs("install");
            Log.d(TAG,"extFile" + extFile);
            printFile(extFile);
            File[] extCache = MainActivity.this.getExternalCacheDirs( ) ;
            Log.d(TAG,"extCache" + extCache);
            printFile(extCache);
            File[] obbDirs = MainActivity.this.getObbDirs();
            Log.d(TAG,"obbDirs" + obbDirs);
            printFile(extCache);
        }
    }

    private void  printFile(File[] files){
        if(files ==null){
            Log.d(TAG,"files null" );
        }
        Log.d(TAG,"----------------------" );
        for (File f:files) {
            Log.d(TAG,"f :" + f.getAbsolutePath());

        }
        Log.d(TAG,"----------------------\n" );
    }
}
