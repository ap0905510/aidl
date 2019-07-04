package com.fcbox.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = PushReceiver.class.getSimpleName();

    public static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED"; //开机启动服务
    public static final String ACTION_START_SERVICE = "fcbox.intent.action.START_SERVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BOOT_ACTION) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            PushManager.getInstance().init(context.getApplicationContext());
        }
    }

}