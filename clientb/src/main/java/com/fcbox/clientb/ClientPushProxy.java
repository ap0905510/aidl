package com.fcbox.clientb;

import android.content.Context;

public class ClientPushProxy {

    private static final String REMOTE_SERVICE_PKG = "com.fcbox.push";
    private static final String REMOTE_SERVICE_ACTION = "com.fcbox.push.PushService";

    PushReceiver pushReceiver;
    ClientBPushLinker linker;

    public ClientPushProxy(Context context) {
        init(context);
    }

    private void init(Context context) {
        pushReceiver = new PushReceiver(context);

        linker = new ClientBPushLinker
                    .Builder(context)
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
