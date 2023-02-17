package com.lemmadev.GPSrecorder;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimerUpdater {
    Timer timer = new Timer();
    public void Update(Context context, int period) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                DataCollector datacollector = new DataCollector();
                Map<String, Object> collectedData = datacollector.getData(null, context);
                // appending to a file the collected data
                try {
                    FileWriter writer = new FileWriter(context.getFilesDir()+"/locations.csv", true);
                    writer.append((CharSequence) collectedData.get("datetime"));
                    writer.append(", ");
                    writer.append((CharSequence) collectedData.get("device"));
                    writer.append(", ");
                    writer.append((CharSequence) collectedData.get("ip"));
                    writer.append(", ");
                    writer.append((CharSequence) collectedData.get("latitude"));
                    writer.append(", ");
                    writer.append((CharSequence) collectedData.get("longitude"));
                    writer.append("\n");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        // Create a TimerTask that will run the desired action
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                handler.sendMessage(message);

            }
        };

        // Create a Timer and schedule the TimerTask to run every 5 seconds
        timer.scheduleAtFixedRate(task, 1000, period);
    }

    public void StopTimer() {
        timer.cancel();
    }
}
