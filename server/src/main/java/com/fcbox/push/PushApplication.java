package com.fcbox.push;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

public class PushApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PushManager.getInstance().init(getApplicationContext());

    }

}
