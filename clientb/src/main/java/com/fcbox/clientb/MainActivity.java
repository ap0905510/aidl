package com.fcbox.clientb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

public class MainActivity extends AppCompatActivity {

    ServiceCallBack serviceCallBack;
    IPushAidlInterface iPushAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceCallBack = new ServiceCallBack();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.fcbox.push", "com.fcbox.push.PushService"));
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iPushAidlInterface = IPushAidlInterface.Stub.asInterface(service);
                try {
                    iPushAidlInterface.registerListener("com.fcbox.clientb", serviceCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    iPushAidlInterface.unregisterListener("com.fcbox.clientb", serviceCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, Context.BIND_AUTO_CREATE);
    }

    class ServiceCallBack extends IPushCallbackAidl.Stub {
        @Override
        public void callback(final String tag, final String message) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuffer sb = new StringBuffer().append("tag=" + tag + "  message=" + message + "\n");
                    Log.e("YW", sb.toString());
                }
            });
        }
    }
}
