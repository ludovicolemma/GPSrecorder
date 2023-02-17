package com.lemmadev.GPSrecorder;

/*
   I didn't know when I started coding that: because of security and privacy policies,
   the clipboard is not accessible anymore while not on the app since Android 10 at least.
   I kept the code because in the end I think it would have worked otherwise.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ClipboardListenerService extends Service {
    private ClipboardManager clipboard;
    private static final int NOTIFICATION_ID = 12345;
    private static final String CHANNEL_ID = "my_channel_id";

    private Context extractedContext = this;

    @Override
    public void onCreate() {
        super.onCreate();
        clipboard = ContextCompat.getSystemService(this, ClipboardManager.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Clipboard Listener Service")
                .setContentText("Service is running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, builder.build());

        final String[] previousClip = {"None"};
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    CharSequence latestUpdate = clip.getItemAt(0).getText();
                    if (!latestUpdate.toString().equals(previousClip[0])) {
                        System.out.println(latestUpdate);
                        previousClip[0] = latestUpdate.toString();
                    }
                }
            }
        });
        return START_STICKY;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Clipboard Listener Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
