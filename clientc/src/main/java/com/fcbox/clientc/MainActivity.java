package com.fcbox.clientc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String REMOTE_SERVICE_PKG = "com.fcbox.rock";
    public static final String REMOTE_SERVICE_ACTION = "com.fcbox.rock.service.PushService";

    PushReceiver pushReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushReceiver = new PushReceiver(this);

        new PushLinker
                .Builder(this)
                .packageName(REMOTE_SERVICE_PKG)
                .action(REMOTE_SERVICE_ACTION)
                .build()
                .bind();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pushReceiver.unregisterReceiver();
    }
}
