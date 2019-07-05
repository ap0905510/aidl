package com.fcbox.push;

import android.app.Application;

public class PushApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PushManager.getInstance().init(getApplicationContext());

    }



}
