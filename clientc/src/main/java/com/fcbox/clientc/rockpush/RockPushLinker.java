package com.fcbox.clientc.rockpush;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.fcbox.rock.aidl.IPushAidlInterface;
import com.fcbox.rock.aidl.IPushCallbackAidl;

public final class RockPushLinker {
    private static final String TAG = "RockPushLinker";

    private ServiceConnection mServiceConnection;
    private Context mContext;
    private String mPackageName;
    private String mAction;
    private String mClassName;
    private IPushAidlInterface mTransferService;
    private IPushCallbackAidl mCallback;

    private RockPushLinker(Context context, String packageName, String action, String className) {
        mContext = context.getApplicationContext();
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
                //todo 回调 子线程
                Log.d(TAG, "Receive callback in client hibox: " + ("tag=" + tag + "  message=" + message));

                String title = "";
                String jpushToken = "";
                /*try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.has(GtPushIntentService.KEY_TITLE)) {
                        title = jsonObject.optString(GtPushIntentService.KEY_TITLE);
                    }
                    if (jsonObject.has(GtPushIntentService.KEY_JPUSHTOKEN)) {
                        jpushToken = jsonObject.optString(GtPushIntentService.KEY_JPUSHTOKEN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!TextUtils.isEmpty(title)) {
                    Intent intent = new Intent(IPushClient.ACTION_MESSAGE_RECEIVED_ROCK);
                    intent.putExtra(IPushClient.EXTRA_MESSAGE, message);
                    intent.putExtra(IPushClient.EXTRA_TITLE, title);
                    intent.putExtra(IPushClient.EXTRA_TOKEN, jpushToken);
                    mContext.sendBroadcast(intent);
                }*/
            }
        };
    }

    /**
     * client send message to server
     */
    public void execute(String tag, String message) {
        try {
            mTransferService.execute(tag, message);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "service connected.");
                mTransferService = IPushAidlInterface.Stub.asInterface(service);
                try {
                    mTransferService.asBinder().linkToDeath(mDeathRecipient, 0);
                    mTransferService.registerListener(mContext.getPackageName(), mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (mTransferService == null) {
                    Log.e(TAG, "Error occur, PushService was null when service disconnected.");
                    return;
                }
                try {
                    mTransferService.unregisterListener(mContext.getPackageName(), mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //mTransferService = null;
            }
        };
    }

    /**
     * remove remote connect service. restart bind
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mTransferService == null) {
                return;
            }
            mTransferService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mTransferService = null;

            //bind();
            Log.e(TAG, "binderDied() : 服务断开, 重连");
        }
    };

    /**
     * Connect to the remote service.
     */
    public void bind() {
        Intent intent = new Intent();
        if (!isStringBlank(mAction)) {
            intent.setAction(mAction);
            // After android 7.0+, service Intent must be explicit.
            intent.setComponent(new ComponentName(mPackageName, mAction));
        } else if (!isStringBlank(mClassName)) {
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
     * params string empty
     */
    static boolean isStringBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Builder to create a new {@link RockPushLinker} instance.
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
         * Create the {@link RockPushLinker} instance using the configured values.
         */
        public RockPushLinker build() {
            if (isStringBlank(mPackageName)) {
                throw new IllegalStateException("Package name required.");
            }
            if (isStringBlank(mAction) && isStringBlank(mClassName)) {
                throw new IllegalStateException("You must set one of the action or className.");
            }
            return new RockPushLinker(mContext, mPackageName, mAction, mClassName);
        }
    }
}
