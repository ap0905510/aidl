package com.fcbox.clientc.rockpush;

import android.content.Context;

public class RockPushProxy {

    public static final String REMOTE_SERVICE_PKG = "com.fcbox.rock";
    public static final String REMOTE_SERVICE_ACTION = "com.fcbox.rock.service.PushService";

    private RockPushReceiver pushReceiver;
    private RockPushLinker linker;
    private Context mContext;

    private RockPushProxy() {
    }

    private static RockPushProxy instance;

    public static RockPushProxy getInstance() {
        if (null == instance) {
            instance = new RockPushProxy();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        pushReceiver = new RockPushReceiver(context);
        linker();
    }

    public void linker() {
        linker = new RockPushLinker
                    .Builder(mContext)
                    .packageName(REMOTE_SERVICE_PKG)
                    .action(REMOTE_SERVICE_ACTION)
                    .build();
        linker.bind();
    }

    public void destroy() {
        if (null != pushReceiver) {
            pushReceiver.unregisterReceiver();
        }
        if (null != linker) {
            linker.unbind();
        }
    }

    public RockPushLinker getLinker() {
        return linker;
    }
}
