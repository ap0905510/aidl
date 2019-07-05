package com.fcbox.push;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PushReceiver extends AbstractReceiver {

    public static final String ACTION_NOTIFY_PUSH_REBIND = "android.intent.action.notify.push.rebind";

    PushReceiver(Context txt) {
        super(txt);
    }

    @Override
    protected void registerReceiver() {
        //动态注册
        IntentFilter inFilter = new IntentFilter();
        inFilter.addAction(ACTION_NOTIFY_PUSH_REBIND);
        inFilter.addAction(ACTION_BOOT);
        inFilter.addAction(ACTION_START_SERVICE);
        mContext.registerReceiver(this, inFilter);
    }

    @Override
    protected void unregisterReceiver() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        switch (intent.getAction()) {
            case ACTION_NOTIFY_PUSH_REBIND:
                //收
                break;
        }
    }
}
