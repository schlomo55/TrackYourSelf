package com.trackyourself;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Time_Up {

    private Context context;
    private static String CHANNEL1_ID = "channel1";
    private static String CHANNEL1_NAME = "Channel 1 Demo";

    private static int id = 1;


    private NotificationManager notificationManager;
    Time_Up(Context context){
        this.context = context;
        createNotificationChannel();
    }


    private void createNotificationChannel()
    {
        //1.Get reference to Notification Manager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //2. createNotificationChannel(mNotificationManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.d("debug", ">>>>>>> " +Build.VERSION.SDK_INT);

            //Create channel only if it is not already created
            if (notificationManager.getNotificationChannel(CHANNEL1_ID) == null)
            {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL1_ID,
                        CHANNEL1_NAME,
                        NotificationManager.IMPORTANCE_HIGH);

                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void showNotification()
    {
        String notificationTitle = "Track Your Self";
        String notificationText =  "Time to go ";

        //3. Build Notification with NotificationCompat.Builder
        //   on Build.VERSION < Oreo the notification avoid the CHANEL_ID
        Notification notification = new NotificationCompat.Builder(context, CHANNEL1_ID)
                .setSmallIcon(android.R.drawable.ic_menu_view)   //Set the icon
                .setContentTitle(notificationTitle) .setSound(null)//Set the title of Notification
               .setContentText(notificationText)    //Set the text for notification
                //.setContentIntent(pendeing intent)
                .build();

        //Send the notification.
        notificationManager.notify(id, notification);
        //id++;
    }

}
