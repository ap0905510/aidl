package com.fcbox.clientc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class PushReceiver extends BroadcastReceiver {

    public static final String REMOTE_SERVICE_PKG = "com.fcbox.rock";
    public static final String REMOTE_SERVICE_ACTION = "com.fcbox.rock.service.PushService";

    public static final String ACTION_NOTIFY_PUSH_REBIND = "android.intent.action.notify.push.rebind";

    private Context mContext;

    PushReceiver(Context txt) {
        this.mContext = txt.getApplicationContext();
        registerReceiver();
    }

    protected void registerReceiver() {
        //动态注册
        IntentFilter inFilter = new IntentFilter();
        inFilter.addAction(ACTION_NOTIFY_PUSH_REBIND);
        mContext.registerReceiver(this, inFilter);
    }

    protected void unregisterReceiver() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_NOTIFY_PUSH_REBIND:
                Log.d("YW", "重连");
                new PushLinker
                        .Builder(mContext)
                        .packageName(REMOTE_SERVICE_PKG)
                        .action(REMOTE_SERVICE_ACTION)
                        .build()
                        .bind();
                break;
        }
    }
}
