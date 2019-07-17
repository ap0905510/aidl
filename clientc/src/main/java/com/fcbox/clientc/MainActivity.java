package com.fcbox.clientc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String REMOTE_SERVICE_PKG = "com.fcbox.rock";
    public static final String REMOTE_SERVICE_ACTION = "com.fcbox.rock.service.PushService";

    PushReceiver pushReceiver;
    PushLinker build;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushReceiver = new PushReceiver(this);

        build = new PushLinker
                .Builder(this)
                .packageName(REMOTE_SERVICE_PKG)
                .action(REMOTE_SERVICE_ACTION)
                .build();
        build.bind();
    }

    /**
     * 测试aidl
     * @param view
     */
    public void send(View view) {
        build.execute(getPackageName(), "客户端发送任务给服务端执行...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pushReceiver.unregisterReceiver();
    }
}
