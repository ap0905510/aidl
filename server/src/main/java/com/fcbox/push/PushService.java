package com.fcbox.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.push.aidl.IPushAidlInterface;
import com.push.aidl.IPushCallbackAidl;

/**
 * @author: 002170
 * @date: 2019/7/4 18:09
 * @desc:
 */
public class PushService extends Service {

    private static final String TAG = "PushService";

    private Context mContext;
    private int JobId;

    public PushService() {
    }

    IPushAidlInterface.Stub binder = new IPushAidlInterface.Stub() {
        @Override
        public void message(String tag, String message) throws RemoteException {
            Log.d(TAG, "tag=" + tag + "  message=" + message);
        }

        @Override
        public void registerListener(String topic, IPushCallbackAidl callback) throws RemoteException {
            Log.d(TAG, "registerListener :: " + topic);
            PushManager.getInstance().getAidlList().put(topic, callback);
//            IBinder iBinder = Stub.asInterface(binder).asBinder();
//            iBinder.linkToDeath(mDeathRecipient, 0);
        }

        @Override
        public void unregisterListener(String topic, IPushCallbackAidl callback) throws RemoteException {
            Log.d(TAG, "unregisterListener");
//            IBinder iBinder = Stub.asInterface(binder).asBinder();
//            iBinder.unlinkToDeath(mDeathRecipient, 0);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        jobStartService(60 * 5 * 1000);
    }

    /**
     * 设置后台拉起PushService
     *
     * @param mill 后台循环启动Service的间隔时间
     */
    private void jobStartService(long mill) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.v(TAG, "jobStartService");

            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(JobId++, new ComponentName(mContext,
                    PushStartJobService.class));
            builder.setPersisted(true);
            builder.setPeriodic(mill);
            //builder.setMinimumLatency(mill); // 设置JobService执行的最小延时时间
            //builder.setOverrideDeadline(mill * 2); // 设置JobService执行的最晚时间
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); //任何可用网络
            scheduler.schedule(builder.build());
        } else {
            Log.v(TAG, "setNotifyWatchdog");
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, PushReceiver.class);
            intent.setAction(PushReceiver.ACTION_START_SERVICE);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long timeNow = SystemClock.elapsedRealtime();
            long nextCheckTime = timeNow + mill; //下次启动的时间
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextCheckTime, pi);
        }
    }


}
