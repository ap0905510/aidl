// IPushAidlInterface.aidl
package com.fcbox.rock.aidl;

import com.fcbox.rock.aidl.IPushCallbackAidl;

// Declare any non-default types here with import statements

interface IPushAidlInterface {
    void execute(String tag, String message);
    void registerListener(String topic, in IPushCallbackAidl callback);
    void unregisterListener(String topic, in IPushCallbackAidl callback);
}
