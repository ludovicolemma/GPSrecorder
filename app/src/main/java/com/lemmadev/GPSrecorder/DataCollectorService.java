package com.lemmadev.GPSrecorder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Map;


public class DataCollectorService extends Service {

    private static final int NOTIFICATION_ID = 1858175;
    private static final String CHANNEL_ID = "data_channel";
    private static final int period = 10000;

    public TimerUpdater timerUpdater;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // TO BIND THE SERVICE AND EVENTUALLY STOP IT
    private final IBinder mBinder = new DataCollectorBinder();

    public class DataCollectorBinder extends Binder {
        public DataCollectorService getService() {
            // Return this instance of DataCollectorService so clients can call public methods
            return DataCollectorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Data Collector Listener Service")
                .setContentText("Service is running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, builder.build());

        timerUpdater = new TimerUpdater();
        timerUpdater.Update(this, period);
        return START_STICKY;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Data Collector Listener Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void stopService() {
        timerUpdater.StopTimer();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
        timerUpdater.StopTimer();
    }
}
