package io.mystudy.tnn.myevmapplication.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationUtil {

    static String createNotificationChannel(Context context, RemoteMessage remoteMessage){
        Map<String, String> data = remoteMessage.getData();

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "channel_reminder_1";    // mockNotificationData.getChannelId();

            // The user-visible name of the channel.
            CharSequence channelName = "Simple Reminder"; // mockNotificationData.getChannelName();
            // The user-visible description of the channel.
            String channelDescription = "Sample Reminder Notifications";// mockNotificationData.getChannelDescription();
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT; // mockNotificationData.getChannelImportance();
            boolean channelEnableVibrate = false; // mockNotificationData.isChannelEnableVibrate();
            int channelLockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC;
                   // mockNotificationData.getChannelLockscreenVisibility();

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
}
