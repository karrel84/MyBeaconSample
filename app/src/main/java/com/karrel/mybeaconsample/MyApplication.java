package com.karrel.mybeaconsample;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Region;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by 몌니저 on 2016-10-02.
 */
public class MyApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        setBeaconManager();
    }

    private void setBeaconManager() {
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {

            @Override
            public void onServiceReady() {
                com.estimote.sdk.Region region =
                        new com.estimote.sdk.Region(
                                "monitored region"
                                , UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D")
                                , 0
                                , 0
                        );
                beaconManager.startMonitoring(region);
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(com.estimote.sdk.Region region, List<Beacon> list) {
                showNotification("들어옴", "비콘 연결됨" + list.get(0).getRssi());
            }

            @Override
            public void onExitedRegion(com.estimote.sdk.Region region) {
                showNotification("나감", "비콘 연결끊김");
            }
        });
    }

    private void showNotification(String title, String msg) {
        Log.d("BeaconTest", String.format("title : %s, msg : %s", title, msg));
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
