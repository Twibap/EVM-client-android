package io.mystudy.tnn.myevmapplication.Application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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


        SharedPreferences sf = getSharedPreferences("Customer", MODE_PRIVATE);

        if ( !sf.contains("firebaseToken") ){
            // 얻어진 토큰은 FirebaseMessagingService의 onNewTokne에서 저장한다.
            FirebaseInstanceId
                    .getInstance()
                    .getInstanceId()
                    .addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            Log.e("newToken",newToken);
                        }
                    });
        }

        Dlog.e("BaseApplication onCreated!");

        FirebaseApp.initializeApp(this);
    }

    public static String getHost_websocket(){
        return "ws://"+url_price+":"+port_price;
    }

    public static String getHost_http() {
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
