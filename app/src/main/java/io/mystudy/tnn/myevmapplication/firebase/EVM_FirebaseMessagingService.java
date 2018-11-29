package io.mystudy.tnn.myevmapplication.firebase;

import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.mystudy.tnn.myevmapplication.Application.Dlog;

public class EVM_FirebaseMessagingService extends FirebaseMessagingService {

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
        Dlog.e("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Dlog.e("Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // scheduleJob();
            } else {
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
    }

}
