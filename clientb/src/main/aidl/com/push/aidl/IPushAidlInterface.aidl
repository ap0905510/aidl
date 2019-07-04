// IPushAidlInterface.aidl
package com.push.aidl;

import com.push.aidl.IPushCallbackAidl;

// Declare any non-default types here with import statements

interface IPushAidlInterface {
    void message(String tag, String message);
    void registerListener(String topic, in IPushCallbackAidl callback);
    void unregisterListener(String topic, in IPushCallbackAidl callback);
}
