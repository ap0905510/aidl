package com.fcbox.clientb;

import android.content.Context;

public class ClientPushProxy {

    private static final String REMOTE_SERVICE_PKG = "com.fcbox.push";
    private static final String REMOTE_SERVICE_ACTION = "com.fcbox.push.PushService";

    private PushReceiver pushReceiver;
    private ClientBPushLinker linker;
    private Context mContext;

    private ClientPushProxy() {
    }

    private static ClientPushProxy instance;

    public static ClientPushProxy getInstance() {
        if (null == instance) {
            instance = new ClientPushProxy();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        pushReceiver = new PushReceiver(context);
        linker();
    }

    public void linker() {
        linker = new ClientBPushLinker
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

    public ClientBPushLinker getLinker() {
        return linker;
    }
}
