package com.github.TwrpBuilder.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.github.TwrpBuilder.Fragment.BackupFragment;
import com.github.TwrpBuilder.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.github.TwrpBuilder.util.Config.getBuildModel;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


        if (remoteMessage.getNotification() != null) {
            String messageBody=remoteMessage.getNotification().getBody();
            System.out.println("FirebaseNotificationService: i'm trigrred "+messageBody);
            if (messageBody.endsWith(getBuildModel())) {
                System.out.println("FirebaseNotificationService: fuck this "+getBuildModel());
                System.out.println("FirebaseNotificationService: this can't be true "+messageBody);
                Intent intent = new Intent(this, BackupFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "1")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("TwrpBuilder")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }
    }
}
