package com.fcbox.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class AbstractReceiver extends BroadcastReceiver {
    private static final String TAG = AbstractReceiver.class.getSimpleName();

    static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED"; //开机启动服务
    static final String ACTION_START_SERVICE = "fcbox.intent.action.START_SERVER"; //JobService

    static final String PARAM_MSG_TIME = "param_msg_time";

    protected Context mContext;

    AbstractReceiver(Context txt) {
        mContext = txt.getApplicationContext();
        registerReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_BOOT) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            PushManager.getInstance().init(context.getApplicationContext());
        }
    }

    protected abstract void registerReceiver();

    protected abstract void unregisterReceiver();

    protected void sendBroadcastImpl(Intent intent) {
        intent.putExtra(PARAM_MSG_TIME, System.currentTimeMillis());
        mContext.sendBroadcast(intent);
    }

}