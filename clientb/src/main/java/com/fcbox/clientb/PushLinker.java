package com.fcbox.clientb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

public final class PushLinker {
    private static final String TAG = "PushLinker";

    private ServiceConnection mServiceConnection;
    private Context mContext;
    private String mPackageName;
    private String mAction;
    private String mClassName;
    private IPushAidlInterface mTransferService;
    private IPushCallbackAidl mCallback;

    private PushLinker(Context context, String packageName, String action, String className) {
        mContext = context;
        mPackageName = packageName;
        mAction = action;
        mClassName = className;
        mServiceConnection = createServiceConnection();
        mCallback = createCallback();
    }

    private IPushCallbackAidl createCallback() {
        return new IPushCallbackAidl.Stub() {
            @Override
            public void callback(String tag, String message) throws RemoteException {
                //todo
                Log.d(TAG, "Receive callback in client:" + message);
                Toast.makeText(mContext, "click " + message, Toast.LENGTH_LONG).show();
            }
        };
    }

    private ServiceConnection createServiceConnection() {
        return new Service();
//        return new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log.d(TAG, "service connected.");
//                mTransferService = IPushAidlInterface.Stub.asInterface(service);
//                try {
//                    mTransferService.registerListener(mPackageName, mCallback);
//                    mTransferService.asBinder().linkToDeath(mDeathRecipient, 0);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                if (mTransferService == null) {
//                    Log.e(TAG, "Error occur, PushService was null when service disconnected.");
//                    return;
//                }
//                try {
//                    mTransferService.unregisterListener(mPackageName, mCallback);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                mTransferService = null;
//            }
//        };
    }

    class Service implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTransferService = IPushAidlInterface.Stub.asInterface(service);
            try {
                mTransferService.registerListener("com.fcbox.clientb", mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mTransferService.unregisterListener("com.fcbox.clientb", mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mTransferService == null) {
                return;
            }
            mTransferService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mTransferService = null;

            bind();
            Log.e("", "binderDied() : 服务断开, 重连");
        }
    };

    /**
     * Connect to the remote service.
     */
    public void bind() {
        Intent intent = new Intent();
        if (!Utils.isStringBlank(mAction)) {
            intent.setAction(mAction);
        } else if (!Utils.isStringBlank(mClassName)) {
            intent.setClassName(mPackageName, mClassName);
        }
        // After android 5.0+, service Intent must be explicit.
        intent.setPackage(mPackageName);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Disconnect from the remote service.
     */
    public void unbind() {
        mContext.unbindService(mServiceConnection);
    }

    /**
     * Builder to create a new {@link PushLinker} instance.
     */
    public static final class Builder {

        private Context mContext;
        private String mPackageName;
        private String mAction;
        private String mClassName;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * Set the remote service package name.
         */
        public Builder packageName(String packageName) {
            mPackageName = packageName;
            return this;
        }

        /**
         * Set the action to bind the remote service.
         */
        public Builder action(String action) {
            mAction = action;
            return this;
        }

        /**
         * Set the class name of the remote service.
         */
        public Builder className(String className) {
            mClassName = className;
            return this;
        }

        /**
         * Create the {@link PushLinker} instance using the configured values.
         */
        public PushLinker build() {
            if (Utils.isStringBlank(mPackageName)) {
                throw new IllegalStateException("Package name required.");
            }
            if (Utils.isStringBlank(mAction) && Utils.isStringBlank(mClassName)) {
                throw new IllegalStateException("You must set one of the action or className.");
            }
            return new PushLinker(mContext, mPackageName, mAction, mClassName);
        }
    }
}