package com.fcbox.clientb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BindingActivity";
    private static final String REMOTE_SERVICE_PKG = "com.fcbox.push";
    private static final String REMOTE_SERVICE_ACTION = "com.fcbox.push.PushService";

    ClientPushProxy mPushProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPushProxy = new ClientPushProxy(this);
    }

    public void send(View view) {
        if (null != mPushProxy) {
            mPushProxy.getLinker().execute(getPackageName(), "客户端向服务端发送消息...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPushProxy.destroy();
        mPushProxy = null;
    }

}
