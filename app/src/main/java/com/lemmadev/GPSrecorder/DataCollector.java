package com.lemmadev.GPSrecorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressLint("MissingPermission")
public class DataCollector extends Activity {

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public Map<String, Object> getData(View view, Context context) {
        LocationManager locationManager;
        Map<String, Object> collectedData = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(calendar.getTime());
        String deviceName = Build.MODEL;
        double latitude = 0;
        double longitude = 0;
        String ip = "0.0.0.0";

        Object finalView = view;
        class SingleThread implements Runnable {
            private volatile String ip;

            @Override
            public void run() {
                ip = "0.0.0.0";
                try {
                    URL connection = new URL("https://checkip.amazonaws.com/");
                    URLConnection con = connection.openConnection();
                    ip = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                } catch (IOException e) {
                    if (view != null) {
                        Snackbar.make(view, "No Ip Found!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }

            public String getValue() {
                return ip;
            }
        }

        SingleThread sth = new SingleThread();
        Thread thread = new Thread(sth);
        thread.start();
        try {
            thread.join();
            ip = sth.getValue();
        } catch (InterruptedException e) {
            ip = "0.0.0.0";
        }

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        String bestProvider = locationManager.getBestProvider(new Criteria(), true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        locationManager.requestLocationUpdates(bestProvider, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Use the location here
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        });

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            latitude = 0.0;
            longitude = 0.0;

            if (view != null) {
                Snackbar.make(view, "No Location Found!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        String lat = latitude + "";
        String lng = longitude + "";

        collectedData.put("device", deviceName);
        collectedData.put("ip", ip);
        collectedData.put("datetime", currentDateTime);
        collectedData.put("latitude", lat);
        collectedData.put("longitude", lng);

        return collectedData;
    }
}