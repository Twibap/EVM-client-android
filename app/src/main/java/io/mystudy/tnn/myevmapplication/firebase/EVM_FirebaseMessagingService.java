package io.mystudy.tnn.myevmapplication.firebase;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.mystudy.tnn.myevmapplication.Application.Dlog;
import io.mystudy.tnn.myevmapplication.R;
import io.mystudy.tnn.myevmapplication.wallet.AccountActivity;

public class EVM_FirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 476;
    private NotificationManagerCompat mNotificationManagerCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Dlog.e("onNewToken: "+s);

        // 생성된 토큰은 저장 후 주문 단계에서 서버로 전송된다.
        SharedPreferences sf = getSharedPreferences("Customer", MODE_PRIVATE);
        sf.edit().putString("firebaseToken", s).apply();
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Dlog.e("onMessageReceived" );

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Dlog.e("FCM message From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        Map<String, String> msgBody = null;
        if (remoteMessage.getData().size() > 0) {
            Dlog.e("Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // scheduleJob();
            } else {
                msgBody = remoteMessage.getData();

                // Handle message within 10 seconds
                // handleNow();
            }
        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Dlog.e("Message Notification Body: "+remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if (msgBody != null) {
            switch (msgBody.get("type")) {
                case "transaction":
                    generateTransactionInfoNotification(remoteMessage);
                    break;
                case "receipt":
                    generateBlockInfoNotification(remoteMessage);
                    break;

                default:
                    Dlog.e("There is no Type in data from FCM MESSAGE!!!");
            }
        }
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that supports both phone/tablet and wear. For devices
     * on API level 16 (4.1.x - Jelly Bean) and after, displays BIG_TEXT_STYLE. Otherwise, displays
     * a basic notification.
     */
    void generateTransactionInfoNotification(RemoteMessage remoteMessage){
        Dlog.e("generate Transaction Notification");

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up main Intent for notification
        //      4. Create additional Actions for the Notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        Map<String, String> data = remoteMessage.getData();

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, remoteMessage);


        // 2. Build the BIG_TEXT_STYLE.
        BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText(data.get("body"))
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(data.get("title"));
        // Summary line after the detail section in the big form of the template.
        // Note: To improve readability, don't overload the user with info. If Summary Text
        // doesn't add critical information, you should skip it.
//                .setSummaryText(bigTextStyleReminderAppData.getSummaryText());


        // 3. Set up main Intent for notification.
        Intent notifyIntent = getIntent( data.get("address") );

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // For the BIG_TEXT_STYLE notification, we will consider the activity launched by the main
        // Intent as a special activity, so we will follow option 2.

        // For an example of option 1, check either the MESSAGING_STYLE or BIG_PICTURE_STYLE
        // examples.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        // 4. Create additional Actions (Intents) for the Notification.

        // In our case, we create two additional actions: a Snooze action and a Dismiss action.
        // txInfo Action.
        Uri txInfoUri = Uri.parse( getString( R.string.url_tx_info_ropsten)+"/"+remoteMessage.getData().get("txHash"));
        Intent txInfoIntent = new Intent(Intent.ACTION_VIEW, txInfoUri);

        PendingIntent txInfoPendingIntent = PendingIntent.getActivity(this, 0, txInfoIntent, 0);
        NotificationCompat.Action txInfoAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_notify_transaction,
                        "Tx",
                        txInfoPendingIntent)
                        .build();


        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for the snooze action, that is, canceling the notification and relaunching
        // it several seconds later.

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        Notification notification = notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigTextStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(data.get("title"))
                // Content for API <24 (7.0 and below) devices.
                .setContentText(data.get("body"))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.mipmap.ic_launcher_round))
                .setContentIntent(notifyPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setCategory(Notification.CATEGORY_REMINDER)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(NotificationCompat.PRIORITY_MAX)

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

                // Adds additional actions specified above.
                .addAction(txInfoAction)

                // Set progress bar
                .setProgress(0,0, true)

                .build();

        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);

    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that supports both phone/tablet and wear. For devices
     * on API level 16 (4.1.x - Jelly Bean) and after, displays BIG_TEXT_STYLE. Otherwise, displays
     * a basic notification.
     */
    void generateBlockInfoNotification(RemoteMessage remoteMessage){
        Dlog.e("generate BlockInfo Notification");

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get your data
        //      1. Create/Retrieve Notification Channel for O and beyond devices (26+)
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up main Intent for notification
        //      4. Create additional Actions for the Notification
        //      5. Build and issue the notification

        // 0. Get your data (everything unique per Notification).
        Map<String, String> data = remoteMessage.getData();

        // 1. Create/Retrieve Notification Channel for O and beyond devices (26+).
        String notificationChannelId =
                NotificationUtil.createNotificationChannel(this, remoteMessage);


        // 2. Build the BIG_TEXT_STYLE.
        BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText(data.get("body"))
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(data.get("title"));
                // Summary line after the detail section in the big form of the template.
                // Note: To improve readability, don't overload the user with info. If Summary Text
                // doesn't add critical information, you should skip it.
//                .setSummaryText(bigTextStyleReminderAppData.getSummaryText());


        // 3. Set up main Intent for notification.
        Intent notifyIntent = getIntent(data.get("address"));

        // When creating your Intent, you need to take into account the back state, i.e., what
        // happens after your Activity launches and the user presses the back button.

        // There are two options:
        //      1. Regular activity - You're starting an Activity that's part of the application's
        //      normal workflow.

        //      2. Special activity - The user only sees this Activity if it's started from a
        //      notification. In a sense, the Activity extends the notification by providing
        //      information that would be hard to display in the notification itself.

        // For the BIG_TEXT_STYLE notification, we will consider the activity launched by the main
        // Intent as a special activity, so we will follow option 2.

        // For an example of option 1, check either the MESSAGING_STYLE or BIG_PICTURE_STYLE
        // examples.

        // For more information, check out our dev article:
        // https://developer.android.com/training/notify-user/navigation.html

        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        // 4. Create additional Actions (Intents) for the Notification.

        // In our case, we create two additional actions: a Snooze action and a Dismiss action.
        // blockInfo Action.
        Uri blockInfoUri = Uri.parse( getString( R.string.url_block_info_ropsten )+"/"+remoteMessage.getData().get("bkHash"));
        Intent blockInfoIntent = new Intent(Intent.ACTION_VIEW, blockInfoUri);

        PendingIntent blockInfoPendingIntent = PendingIntent.getActivity(this, 0, blockInfoIntent, 0);
        NotificationCompat.Action blockInfoAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_notify_block,
                        "Block",
                        blockInfoPendingIntent)
                        .build();


        // txInfo Action.
        Uri txInfoUri = Uri.parse( getString( R.string.url_tx_info_ropsten)+"/"+remoteMessage.getData().get("txHash"));
        Intent txInfoIntent = new Intent(Intent.ACTION_VIEW, txInfoUri);

        PendingIntent txInfoPendingIntent = PendingIntent.getActivity(this, 0, txInfoIntent, 0);
        NotificationCompat.Action txInfoAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.ic_notify_transaction,
                        "Tx",
                        txInfoPendingIntent)
                        .build();


        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for the snooze action, that is, canceling the notification and relaunching
        // it several seconds later.

        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), notificationChannelId);

        Notification notification = notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigTextStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(data.get("title"))
                // Content for API <24 (7.0 and below) devices.
                .setContentText(data.get("body"))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(),
                        R.mipmap.ic_launcher_round))
                .setContentIntent(notifyPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setCategory(Notification.CATEGORY_REMINDER)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(NotificationCompat.PRIORITY_MAX)

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

                // Adds additional actions specified above.
                .addAction(txInfoAction)
                .addAction(blockInfoAction)

                // Finish progress bar
                .setProgress(0,0, false)

                .build();

        mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);

    }

    /**
     * AccountActivity로 이동하는 Intent를 생성한다.
     * Intent에는 FCM에 포함된 이더 주문자 주소를 담는다.
     * @param address
     * @return
     */
    private Intent getIntent(String address){
        Intent result = new Intent(this, AccountActivity.class);
        result.putExtra("account", address);
        result.putExtra("isFromService", true);
        return result;
    }

}
