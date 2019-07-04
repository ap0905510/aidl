package com.fcbox.push;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

public class PushService extends Service {

    private static final String TAG = "PushService";
    private RemoteCallbackList<IPushCallbackAidl> callbackList = new RemoteCallbackList<>();//回调的关键（API>=17,才能使用）

    public PushService() {
    }

    IPushAidlInterface.Stub binder = new IPushAidlInterface.Stub() {
        @Override
        public void message(String tag, String message) throws RemoteException {
            //beginBroadcast和finishBroadcast一定要成对出现，
            callbackList.beginBroadcast();
            sendMessageToAllClient(tag, message);
            Log.d(TAG, "tag=" + tag + "  message=" + message);
            callbackList.finishBroadcast();
        }

        @Override
        public void registerListener(String topic, IPushCallbackAidl callback) throws RemoteException {
            callbackList.register(callback);//注册回调listener
            Log.d(TAG, "registerListener :: " + topic);

            PushManager.getInstance().getAidlList().put(topic, callback);
        }

        @Override
        public void unregisterListener(String topic, IPushCallbackAidl callback) throws RemoteException {
            callbackList.unregister(callback);//取消回调listener
            Log.d(TAG, "unregisterListener");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //PushManager.getInstance().rollMore();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发送消息给全部的client（你也可以指定发送给某个client,也可
     * 以根据自己的业务来封装一下Bean，记得要实现Parcelable接口来序列化
     *
     * @param tag
     * @param message
     */
    @SuppressLint("NewApi")
    private void sendMessageToAllClient(String tag, String message) {
        for (int i = 0; i < callbackList.getRegisteredCallbackCount(); i++) {
            try {
                callbackList.getBroadcastItem(i).callback(tag, message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
