package com.fcbox.push;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

@TargetApi(21)
public class PushStartJobService extends JobService {
    private static final String TAG = PushStartJobService.class.getSimpleName();
    private static final int START_SERVER = 1;

    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case START_SERVER:
                    Intent intent = new Intent(getApplicationContext(), PushReceiver.class);
                    intent.setAction(PushReceiver.ACTION_START_SERVICE);
                    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intent);
                    break;
            }
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });


    /**
     * 服务启动时系统将会调用本方法
     *
     * @param params
     * @return false表示后台设置的方法执行完毕，
     * true表示后台设置的方法未执行完毕，一般设为false即可
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Message msg = mJobHandler.obtainMessage();
        msg.what = START_SERVER;
        msg.obj = params;
        mJobHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(START_SERVER);
        return false;
    }
}
