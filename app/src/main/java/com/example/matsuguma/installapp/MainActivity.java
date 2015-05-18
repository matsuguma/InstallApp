package com.example.matsuguma.installapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    // サービス起動時のインテント
    private Intent serviceIntent = null;

    // サービス
    private IInstallService mService = null;

    // プロセス間通信のためのコネクション
    private ServiceConnection connect = new ServiceConnection() {

        // コネクション開始時
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // サービス起動
            mService = IInstallService.Stub.asInterface(service);
        }

        // サービスが異常終了した時
        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(connect);
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // クリックリスナーを設定
        this.findViewById(R.id.service_start_button).setOnClickListener(this);
        this.findViewById(R.id.service_end_button).setOnClickListener(this);

        // 初期状態ではサービス終了ボタンは非活性とする
        this.findViewById(R.id.service_end_button).setEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * クリックイベント
     */
    public void onClick(View view) {
        int clickedViewId = view.getId();

        if (clickedViewId == R.id.service_start_button) {
            serviceIntent = new Intent(IInstallService.class.getName());

            // サービスとの接続を開始する
            bindService(serviceIntent, connect, BIND_AUTO_CREATE);

            this.findViewById(R.id.service_start_button).setEnabled(false);
            this.findViewById(R.id.service_end_button).setEnabled(true);

        } else if (clickedViewId == R.id.service_end_button) {
            // サービスとの接続を切断する
            unbindService(connect);
            mService = null;

            this.findViewById(R.id.service_start_button).setEnabled(true);
            this.findViewById(R.id.service_end_button).setEnabled(false);
        }
    }
}
