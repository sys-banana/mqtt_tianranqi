package com.example.zhuang.mqtt.base;

import android.app.Application;



// 尽量不要使用此方法
public class Applications {
    private static final String TAG = "Applications";

    public static Application getCurrent() {
        return CURRENT;
    }

    public  static  Application CURRENT;
    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null) throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            // Alternative path
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
            }
        } finally { CURRENT = app; }
    }
}
