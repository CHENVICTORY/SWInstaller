package net.sunniwell.swinstaller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 实际进行安装的task
 *
 */
public class PackageInstallerT extends AsyncTask<String,String,String> {
public static final String TAG = "PackageInstaller";
public static final String ACTION_INSTALL_COMPLETE = "net.sunniwell.actionInstallDone";
private Context ctx ;

public PackageInstallerT(Context ctxt){
    super();
    ctx = ctxt;
}



    @Override
    protected void onPreExecute() {
        Log.d(TAG,"Start to install ");
    }

    @Override
    protected String doInBackground(String... strings) {
        File appFile = new File(strings[0]);
        if(appFile.exists()){
            PackageInstaller packageInstaller =ctx.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new
                    PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName("com.tencent.qqmusictv");
            try {
                int sessionId = packageInstaller.createSession(params);
                PackageInstaller.Session session = packageInstaller.openSession(sessionId);
                OutputStream out = session.openWrite("sunniwellInstaller",0,-1);
                FileInputStream fis = new FileInputStream(appFile);
                byte[] buffer = new byte[65535];
                int c  =-1;
                while ((c = fis.read(buffer)) !=-1){
                    out.write(buffer,0,c);
                }
                session.fsync(out);
                fis.close();
                out.close();
                session.commit(createIntentSender(ctx,sessionId));
                return "success";
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private static IntentSender createIntentSender(Context context, int sessionId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(ACTION_INSTALL_COMPLETE),
                0);
        return pendingIntent.getIntentSender();
    }

    @Override
    protected void onCancelled() {
    }

    @Override
    protected void onCancelled(String s) {
    }

    @Override
    protected void onPostExecute(String s) {
    }
}
