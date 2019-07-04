package com.fcbox.push;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushManager {

    private PushManager() {}

    private static class SingleTon {
        static final PushManager instance = new PushManager();
    }

    public static PushManager getInstance() {
        return SingleTon.instance;
    }

    static final Map<String, IPushCallbackAidl> mAidlMap = new ConcurrentHashMap<>();
    private Context mContext;

    public void init(Context context) {
        this.mContext = context;
        startService();
    }

    public void startService() {
        Intent in = new Intent(mContext, PushService.class);
        mContext.startService(in);//启动服务
        Intent intent = new Intent(mContext, PushService.class);
        mContext.bindService(intent, mDaemonConnection, Context.BIND_AUTO_CREATE);
    }

    public Map<String, IPushCallbackAidl> getAidlList() {
        return mAidlMap;
    }

    public void roll(String topic, String msg) {
        for (Map.Entry<String, IPushCallbackAidl> entry : mAidlMap.entrySet()) {
            try {
                Log.e("YW", "topic: " + topic + " :: " + entry.getKey() + " :: " + entry.getValue());
                IPushCallbackAidl callBack = entry.getValue();
                callBack.callback("tag", "---msg :: " + msg);
            } catch (RemoteException e) {
                e.printStackTrace();
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

    IPushAidlInterface mPushListener;

    private ServiceConnection mDaemonConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPushListener = IPushAidlInterface.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (Exception e) {
                Log.e("", "[daemon] 守护服务的死亡守护连接断开, " + e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPushListener = null;
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mPushListener == null) {
                return;
            }
            mPushListener.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mPushListener = null;

            Intent intent = new Intent(mContext, PushService.class);
            mContext.bindService(intent, mDaemonConnection, Context.BIND_AUTO_CREATE);
            Log.e("", "binderDied() : 守护服务断开, 重连");
        }
    };
}
