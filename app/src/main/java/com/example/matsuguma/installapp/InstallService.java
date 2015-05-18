package com.example.matsuguma.installapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by matsuguma on 2015/05/13.
 */
public class InstallService extends Service {

    // 初回の遅延時間(ms)
    private long DELAY_TIME = 2007;

    // タイマー間隔(ms)
    private long TIMER_INTERVAL = 20000;

    // インタフェースの実装
    private IInstallService.Stub mService = new IInstallService.Stub() {
        /*
            @Override
            public int add(int x, int y) throws RemoteException {
                return x + y;
            }
        */
        };

        // timer
        private Timer timer = new Timer();

        private TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // TODO バージョンがアップされたときだけ
                if (checkVersion()) {


                    // TODO インストールが完了したときだけ通知するようにすれば良いだろうか？
                    // TODO それとも通知自体不要だろうか？
                    // 通知処理
                    // sendNotification();

                    installApp();
                }
            }
        };

        @Override
        public void onCreate() {
            super.onCreate();

            // 初回だけ10秒後に実行
            timer.schedule(task, DELAY_TIME, TIMER_INTERVAL);
        }

        // サービス開始時にコール
        @Override
        public IBinder onBind(Intent intent) {
            Toast.makeText(this, "サービス-onBind()", Toast.LENGTH_SHORT).show();
            return mService;
        }

        // サービス終了時にコール
        @Override
        public boolean onUnbind(Intent intent) {
            Toast.makeText(this, "サービス-onUnbind()", Toast.LENGTH_SHORT).show();
            return super.onUnbind(intent);
        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "サービス-onDestroy()", Toast.LENGTH_SHORT).show();
            // タイマーを破棄する
            timer.cancel();

            super.onDestroy();
        }

    /**
     * バージョンがアップされたか否かチェックする
     * @return 判定結果
     */
    private boolean checkVersion() {
        // TODO チェック処理
        return true;
    }

    /**
     * ステータスバーに通知する
     */
    private void sendNotification() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/matsuguma/" + "app-release.apk");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            // startActivity(intent);
            PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, intent, 0);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle("ダウンロード完了しました。")
                    .setContentText("アプリのダウンロードが完了しました。")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.default_image)
                    .setAutoCancel(true)
                    .setTicker("ダウンロード完了しました。")
                    .build();

            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            nm.notify(1000, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * アプリをインストールする
     */
    private void installApp(){
        try {

//            String filename = Environment.getExternalStorageDirectory() + "/matsuguma/" + "app-release.apk";
            String filename = "/sdcard/matsuguma/" + "app-release.apk";

            File file = new File(filename);

            if(file.exists()){
                try {
                    String command;
                    // filename = StringUtil.insertEscape(filename);
                    // command = "pm install -r " + file.getAbsolutePath();
                    // command = "pm install -r " + file;
                    command = "java -version";
                    // Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                    Process proc = Runtime.getRuntime().exec(new String[] { "su" });
                    proc.waitFor();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
