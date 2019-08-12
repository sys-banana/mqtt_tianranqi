package com.example.zhuang.mqtt.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.zhuang.mqtt.MainActivity;

public class NotificationUtilsMe/* extends ContextWrapper*/ {
    public static final String channelId = "channel_1";
    public static final String channelName = "channel_name_1";

    private Context context;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification.Builder builder26;
    private int notifyId=1;
    PendingIntent pendingIntent;
    public NotificationUtilsMe(Context context){
        //super(context);
        this.context=context;

        Intent intent=new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);

        getManager().createNotificationChannel(channel);
    }
    private NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String content){
        createNotificationChannel();
        builder26= new Notification.Builder(context.getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        ;
        return builder26;
    }
    public NotificationCompat.Builder getNotification_25(String title, String content){
        builder= new NotificationCompat.Builder(context.getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        ;
        return builder;
    }
    int time=0;
    public void sendNotification(final String title, String content){
        final Notification notification;
        if (Build.VERSION.SDK_INT>=26){
            //createNotificationChannel();
            notification = getChannelNotification(title, content).build();
        }else{
            notification = getNotification_25(title, content).build();
        }
        getManager().notify(notifyId,notification);

    }
    public void updateProgress(int progress){
        if(time<100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder26.setProgress(100, time++, false);
                builder26.setContentInfo(time+"%");
                notificationManager.notify(notifyId,builder26.build());
            } else {
                builder.setProgress(100, time++, false);
                builder.setContentInfo(time+"%");
                notificationManager.notify(notifyId,builder.build());
            }
        }
        else {

        }
    }
}