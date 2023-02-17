package com.lemmadev.GPSrecorder;

import static android.app.PendingIntent.getService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.lemmadev.GPSrecorder.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String SERVICE_RUNNING_KEY = "15399";
    boolean serviceRunning = false;
    private Intent serviceIntent = null;
    Context context = this;

    // Creating a connection to the service, so that it can be stopped.
    public ServiceConnection CreateConnection() {

        ServiceConnection mConnection = new ServiceConnection() {

            DataCollectorService dataCollectorService;
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                // The service is connected, cast the IBinder to your service class
                DataCollectorService.DataCollectorBinder binder = (DataCollectorService.DataCollectorBinder) service;
                dataCollectorService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // Call a method on the service to stop it
                dataCollectorService.stopService();
            }
        };
        return mConnection;
    }

    ServiceConnection aConnection;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        askPermissions();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        serviceRunning = sharedPreferences.getBoolean(SERVICE_RUNNING_KEY, false);

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch serviceSwitch = findViewById(R.id.switch2);
        serviceSwitch.setChecked(serviceRunning);


        if (serviceRunning) {
            // Start the service
            serviceIntent = new Intent(context, DataCollectorService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
            aConnection = CreateConnection();
            context.bindService(serviceIntent, aConnection, Context.BIND_AUTO_CREATE);
        }

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked && !serviceRunning) {
                    // Start the service
                    serviceIntent = new Intent(context, DataCollectorService.class);
                    ContextCompat.startForegroundService(context, serviceIntent);
                    aConnection = CreateConnection();
                    context.bindService(serviceIntent, aConnection, Context.BIND_AUTO_CREATE);
                    serviceRunning = true;
                } else if (!isChecked && serviceRunning) {
                    // Stop the service
                    context.unbindService(aConnection);
                    context.stopService(serviceIntent);
                    serviceIntent = null;
                    serviceRunning = false;
                }
                // Store the state of the service in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SERVICE_RUNNING_KEY, serviceRunning);
                editor.apply();
            }
        });
    }

    /* I don't need it right now.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void askPermissions() {
        // First, check for ACCESS_FINE_LOCATION permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // If permission is already granted, move on to the next permission
            requestBackgroundLocationPermission();
        }
    }

    // Function to request ACCESS_BACKGROUND_LOCATION permission
    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // If permission is not granted and device API level is 29 or higher, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
        } else {
            // If permission is already granted or device API level is lower than 29, move on to the next permission
            requestReadExternalStoragePermission();
        }
    }

    // Function to request READ_EXTERNAL_STORAGE permission
    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
        // No need to handle the case where permission is already granted, as this is the last permission to be requested
    }

    // Override onRequestPermissionsResult to handle the results of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // Handle result for ACCESS_FINE_LOCATION permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, move on to the next permission
                requestBackgroundLocationPermission();
            }
        } else if (requestCode == 2) {
            // Handle result for ACCESS_BACKGROUND_LOCATION permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, move on to the next permission
                requestReadExternalStoragePermission();
            }
        } else if (requestCode == 3) {
            // Handle result for READ_EXTERNAL_STORAGE permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, all required permissions have been granted
            }
        }
    }

}