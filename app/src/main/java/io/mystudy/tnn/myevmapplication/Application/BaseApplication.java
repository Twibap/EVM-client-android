package io.mystudy.tnn.myevmapplication.Application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import io.mystudy.tnn.myevmapplication.R;
import io.mystudy.tnn.myevmapplication.websocket.Price;

public class BaseApplication extends Application {
    public static boolean DEBUG = false;

    public static String url_price;
    public static String url_order;
    public static String port_price;
    public static String port_order;

    private NotificationManagerCompat mNotificationManagerCompat;
    boolean areNotificationsEnabled;

    // 사용자 정보
    private String address;

    // 이더 시세
    private Price etherPrice;

    @Override
    public void onCreate() {
        super.onCreate();
        this.DEBUG = isDebuggable(this);

        url_price = getString(R.string.url_price);
        url_order = getString(R.string.url_order);
        port_price = getString(R.string.port_price);
        port_order = getString(R.string.port_order);

        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        areNotificationsEnabled = mNotificationManagerCompat.areNotificationsEnabled();

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

    /**
     * 사용자에게 Notification 권한을 요구한다.
     *
     * @param view 권한을 요청하는 SnackBar를 표시할 화면
     */
    public void requestOpenNotificationSetting(View view){
        if (!areNotificationsEnabled) {
            // Because the user took an action to create a notification, we create a prompt to let
            // the user re-enable notifications for this application again.
            Snackbar snackbar = Snackbar
                    .make(
                            view,
                            "You need to enable notifications for this app",
                            Snackbar.LENGTH_LONG)
                    .setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Links to this app's notification settings
                            openNotificationSettingsForApp();
                        }
                    });
            snackbar.show();
        }
    }

    /**
     * Helper method for the SnackBar action, i.e., if the user has this application's notifications
     * disabled, this opens up the dialog to turn them back on after the user requests a
     * Notification launch.
     *
     * IMPORTANT NOTE: You should not do this action unless the user takes an action to see your
     * Notifications like this sample demonstrates. Spamming users to re-enable your notifications
     * is a bad idea.
     */
    private void openNotificationSettingsForApp() {
        // Links to this app's notification settings.
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEtherPrice(Price etherPrice) {
        this.etherPrice = etherPrice;
    }

    public String getAddress() {
        return address;
    }

    public Price getEtherPrice() {
        return etherPrice;
    }
}
