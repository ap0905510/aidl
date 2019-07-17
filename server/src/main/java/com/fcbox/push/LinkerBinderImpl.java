package com.fcbox.push;

import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

public class LinkerBinderImpl extends IPushAidlInterface.Stub implements PushLinkerBinder {

    private static final String TAG = "LinkerBinder";
    private RemoteCallbackList<IPushCallbackAidl> mCallbackList;

    LinkerBinderImpl() {
        mCallbackList = new RemoteCallbackList<>();
    }

    @Override
    public void execute(String tag, String message) throws RemoteException {
        Log.d(TAG, "Receive request: " + message);
    }

    @Override
    public void registerListener(String topic, IPushCallbackAidl callback) throws RemoteException {
        int pid = Binder.getCallingPid();
        Log.d(TAG, "register callback: " + callback + " pid: " + pid);
        if (callback != null) {
            mCallbackList.register(callback, pid);
            PushManager.getInstance().getAidlCallbackMap().put(topic, callback);
        }
    }

    @Override
    public void unregisterListener(String topic, IPushCallbackAidl callback) throws RemoteException {
        int pid = Binder.getCallingPid();
        Log.d(TAG, "unRegister callback: " + callback + " pid: " + pid);
        if (callback != null) {
            mCallbackList.unregister(callback);
            PushManager.getInstance().getAidlCallbackMap().remove(topic);
        }
    }

    public void in() {
        final int len = mCallbackList.beginBroadcast();
        for (int i = 0; i < len; i++) {
            int cookiePid = (int) mCallbackList.getBroadcastCookie(i);

            try {
                mCallbackList.getBroadcastItem(i).callback("","");
            } catch (RemoteException e) {
                Log.e(TAG, "Error when execute callback!", e);
            }
            break;
        }
        mCallbackList.finishBroadcast();
    }

}
