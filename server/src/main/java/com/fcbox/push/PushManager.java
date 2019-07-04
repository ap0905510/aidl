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

import java.util.ArrayList;
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
    private IPushAidlInterface iPushAidlInterface;

    public Map<String, IPushCallbackAidl> getAidlList() {
        return mAidlMap;
    }

    public void bindServiceConnection(Context context) {
        Intent intent = new Intent(context, PushService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iPushAidlInterface = IPushAidlInterface.Stub.asInterface(service);
                try {
                    iPushAidlInterface.message("tag", "server");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    public void push(String message) throws RemoteException {
        iPushAidlInterface.message("tag", message);
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
}
