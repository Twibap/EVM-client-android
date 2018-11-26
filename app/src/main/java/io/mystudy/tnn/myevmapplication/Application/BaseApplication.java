package io.mystudy.tnn.myevmapplication.Application;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import io.mystudy.tnn.myevmapplication.R;

public class BaseApplication extends Application {
    public static boolean DEBUG = false;

    public static String url_price;
    public static String url_order;
    public static String port_price;
    public static String port_order;

    @Override
    public void onCreate() {
        super.onCreate();
        this.DEBUG = isDebuggable(this);

        url_price = getString(R.string.url_price);
        url_order = getString(R.string.url_order);
        port_price = getString(R.string.port_price);
        port_order = getString(R.string.port_order);

        Dlog.e("BaseApplication onCreated!");
    }

    public static String getHost_price(){
        return "ws://"+url_price+":"+port_price;
    }

    public static String getHost_order() {
        return "http://"+url_order+":"+port_order;
    }

    private boolean isDebuggable(Context context){
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
        }
        return debuggable;
    }
}
