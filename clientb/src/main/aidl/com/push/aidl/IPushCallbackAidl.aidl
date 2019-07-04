// IPushCallbackAidl.aidl
package com.push.aidl;

// Declare any non-default types here with import statements

interface IPushCallbackAidl {
    void callback(String tag, String message);
}
