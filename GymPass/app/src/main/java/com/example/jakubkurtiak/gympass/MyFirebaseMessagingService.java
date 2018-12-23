package com.example.jakubkurtiak.gympass;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


// -----------------------------------------
// Firebase push notifications.
// -----------------------------------------

// class that extends FirebaseMessagingService To receive notification from firebase
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Override onMessageReceived
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pI = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        //sound for notification
        Uri nSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //build notification
        NotificationCompat.Builder nB = new NotificationCompat.Builder(this);
        nB.setContentTitle("GYMPASS NOTIFICATION");
        nB.setContentText(remoteMessage.getNotification().getBody());
        nB.setColor(0xf46524); //(0xfffff00f);
        nB.setSound(nSound);
        nB.setAutoCancel(true);
        nB.setSmallIcon(R.mipmap.ic_launcher);
        nB.setContentIntent(pI);

        //Build & issue notification
        NotificationManager nM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nM.notify(0,nB.build());
    }

}
