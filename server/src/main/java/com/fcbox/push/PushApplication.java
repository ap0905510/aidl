package com.fcbox.push;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

public class PushApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PushManager.getInstance().init(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PushReceiver pushReceiver = new PushReceiver(PushApplication.this);
                pushReceiver.sendBroadcastImpl(new Intent(PushReceiver.ACTION_NOTIFY_PUSH_REBIND));
            }
        }, 2000);


    }

}
