package com.sharepark;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;

import com.sharepark.util.NotificationPublisher;

/**
 * Created by tipqc on 5/12/2016.
 */
public class NotifHelper {

    public static void scheduleNotification(Notification notification, long delay, Context mAct, int id) {

        Intent notificationIntent = new Intent(mAct, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mAct, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) mAct.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static Notification getNotification(String content, Context mAct, String res_id,String thumbnail) {
        Notification.Builder builder = new Notification.Builder(mAct);
        builder.setContentTitle("Hi Parker!");
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_drive_eta_black_24dp);
        builder.setLargeIcon(BitmapFactory.decodeResource(mAct.getResources(),
                R.mipmap.ic_launcher));
        //Intent resultIntent = new Intent(mAct, IndividualReservation.class);
        /**/
        Bundle resData = new Bundle();

       /* resData.putString("b_cp_id",cp_id);
        resData.putString("b_resCompanyName",cp_name);
        resData.putString("b_cp_lat",cp_lat);
        resData.putString("b_cp_lng",cp_lng);
        resData.putString("b_resCheckIn",checkin);
        resData.putString("b_resCheckOut",checkout);
        resData.putString("b_resDate",date);
        resData.putString("b_resCode",code);
        resData.putString("b_carPlate",carplate);
        resData.putString("b_resType", restype);*/
        resData.putString("b_resID", res_id);/*
        resData.putString("b_thumbnail", Constants.web_tip+"images/"+thumbnail+".png");*/
        //resultIntent.putExtras(resData);

       /* PendingIntent resultPendingIntent = PendingIntent.getActivity(
                        mAct,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );*/
        //builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }
}
