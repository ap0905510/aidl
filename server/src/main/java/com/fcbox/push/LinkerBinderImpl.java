package com.fcbox.push;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

public class LinkerBinderImpl extends IPushAidlInterface.Stub implements PushLinkerBinder {

    private static final String TAG = "LinkerBinderImpl";

    LinkerBinderImpl() {
    }

    @Override
    public void execute(String tag, String message) throws RemoteException {
        Log.d(TAG, "Receive request: " + tag + " :: " + message);
    }

    @Override
    public void registerListener(String topic, IPushCallbackAidl callback) throws RemoteException {
        Log.d(TAG, "register topic: " + topic + " callback: " + callback + " pid: " + Binder.getCallingPid());
        if (callback != null) {
            PushManager.getInstance().getAidlCallbackMap().put(topic, callback);
        }
        try {
            IBinder iBinder = callback.asBinder();
            iBinder.linkToDeath(new DeathCallback(topic), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterListener(String topic, IPushCallbackAidl callback) throws RemoteException {
        Log.d(TAG, "unRegister topic: " + topic + " callback: " + callback);
        if (callback != null) {
            PushManager.getInstance().getAidlCallbackMap().remove(topic);
        }
        try {
            IBinder iBinder = callback.asBinder();
            iBinder.unlinkToDeath(new DeathCallback(topic), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DeathCallback implements IBinder.DeathRecipient {
        String topic;

        public DeathCallback(String tag) {
            this.topic = tag;
        }

        @Override
        public void binderDied() {
            PushManager.getInstance().getAidlCallbackMap().remove(topic);
        }
    }
}
