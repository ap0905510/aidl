package com.fcbox.push;

import android.os.IBinder;

/**
 * AndLinker {@link IBinder} object to return in {@link android.app.Service#onBind(android.content.Intent)} method.
 */
public interface PushLinkerBinder extends IBinder {

    /**
     * {@link PushLinkerBinder} factory class.
     */
    final class Factory {

        private Factory() {
            
        }
        
        /**
         * Factory method to create the {@link PushLinkerBinder} impl instance.
         */
        public static PushLinkerBinder newBinder() {
            // Return inner package access LinkerBinder, prevent exposed.
            return new LinkerBinderImpl();
        }
    }
    
}
