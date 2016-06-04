package com.example.coursemanagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * It initializes the reminder and creates the notification on the specified time
 */
public class OnAlarmReceiver extends Service {

    private static final String TAG = "OnAlarmReceiver";
    String subject, deadline, user;
    private NotificationManager nm;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.v(TAG, "on onCreate");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            subject = bundle.getString("subject_key");
            deadline = bundle.getString("deadline_key");
            user = bundle.getString("user_key");
        }
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.v(TAG, "on onStartCommand");
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        //setting up notification
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(subject)
                .setContentText("Assignment Deadline :" + deadline)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        nm.notify(0, notification);
        Toast.makeText(getApplicationContext(), "Alarm has begun", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Alarm has begun");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
