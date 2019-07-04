package com.fcbox.push;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;

public class PushApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PushManager.getInstance().init(getApplicationContext());

    }



}
