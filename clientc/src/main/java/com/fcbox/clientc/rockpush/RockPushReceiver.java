package com.fcbox.clientc.rockpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class RockPushReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFY_PUSH_REBIND = "fcbox.intent.action.rock.push.rebind";

    private Context mContext;

    RockPushReceiver(Context txt) {
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
                Log.d("push", "重连");
                RockPushProxy.getInstance().linker();
                break;
        }
    }
}
