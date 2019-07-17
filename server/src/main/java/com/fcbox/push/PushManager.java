package com.fcbox.push;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.push.aidl.IPushCallbackAidl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushManager {
    public static final String REMOTE_SERVICE_PKG = "com.fcbox.push";
    public static final String REMOTE_SERVICE_ACTION = "com.fcbox.push.PushService";

    private PushManager() {
    }

    private static class SingleTon {
        static final PushManager instance = new PushManager();
    }

    public static PushManager getInstance() {
        return SingleTon.instance;
    }

    static final Map<String, IPushCallbackAidl> mAidlMap = new ConcurrentHashMap<>();
    private Context mContext;
    PushLinker mPushLinker;

    public void init(Context context) {
        this.mContext = context;
        startService();
    }

    public void startService() {
        Intent in = new Intent(mContext, PushService.class);
        mContext.startService(in);//启动服务
        mPushLinker = new PushLinker
                .Builder(mContext)
                .packageName(REMOTE_SERVICE_PKG)
                .action(REMOTE_SERVICE_ACTION)
                .build();
        mPushLinker.bind();
    }

    public void unbind() {
        if (null != mPushLinker) {
            mPushLinker.unbind();
        }
    }

    public Map<String, IPushCallbackAidl> getAidlCallbackMap() {
        return mAidlMap;
    }

    public void roll(String topic, String msg) {
        for (Map.Entry<String, IPushCallbackAidl> entry : mAidlMap.entrySet()) {
            if (topic.equalsIgnoreCase(entry.getKey())) {
                try {
                    Log.e("YW", "topic: " + topic + " :: " + entry.getKey() + " :: " + entry.getValue());
                    IPushCallbackAidl callBack = entry.getValue();
                    callBack.callback("tag", "---msg :: " + msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rollMore() {
        int count = 0;
        while (count++ < 20) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            roll("", count + "");
        }
    }
}
