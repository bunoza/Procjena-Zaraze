package com.bunoza.procjenazaraze2.location_provider;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.repo.Repository;
import com.bunoza.procjenazaraze2.ui.preferences.SettingsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class GoogleService extends Service implements LocationListener {

    public final String TAG = "Google service";
    boolean isGPSEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    ArrayList<LocationsModel> locations;
    Location location;
    Repository repo;
    private Handler mHandler;
    private Timer mTimer = null;
    SharedPreferences sharedPreferences;
    long notify_interval = 5000;
    TimerTaskToGetLocation mTimerToGetLoc;


    public GoogleService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("gpsSwitch", false)) {
            notify_interval = 1000 * Integer.parseInt(sharedPreferences.getString(
                    "interval", "1000"));

            mHandler = new Handler();
            mTimer = new Timer();
            mTimerToGetLoc = new TimerTaskToGetLocation();
            mTimer.schedule(mTimerToGetLoc, 5, notify_interval);
            repo = Repository.getInstance();
            fn_getlocation();

            Log.d(TAG, "onCreate: " + notify_interval);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground();
            else
                startForeground(1, new Notification());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d(TAG, "fn_getlocation: " + sharedPreferences.getBoolean("gpsSwitch", false));

        if(!sharedPreferences.getBoolean("gpsSwitch", false)){
            Log.d(TAG, "fn_getlocation: GASIM");
            stopForeground(true);
            stopSelf();
        }

        if (isGPSEnable) {
            location = null;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.d("latitude",location.getLatitude()+"");
                        Log.d("longitude",location.getLongitude()+"");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }


//        }

    }

    private class TimerTaskToGetLocation extends TimerTask{
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    if(sharedPreferences.getBoolean("gpsSwitch", false)){
                        fn_getlocation();
                    }else {
                        stopForeground(true);
                        cancel();
                    }
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fn_update(Location location) {
        if(!sharedPreferences.getBoolean("gpsSwitch", false)){
            stopForeground(true);
            stopSelf();
        }
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        int i;
        try{
            String currentAddress = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1).get(0).getAddressLine(0);
        if (repo.getLocationsDead() != null && repo.getLocationsDead().size() > 0){
            if(!currentAddress.equals(repo.getLocationsDead().get(repo.getLocationsDead().size()-1)
                    .address)) {
                locations = new ArrayList<>();
                for(i = 0; i < repo.getLocationsDead().size(); i++){
                    locations.add(repo.getLocationsDead().get(i));
                }
                locations.add(new LocationsModel(String.valueOf(new Date().getTime()),
                        currentAddress));
                LocationsModel temp = new LocationsModel(String.valueOf(new Date().getTime()),
                        currentAddress);
                repo.insertData(temp);
            }
        }else{
            LocationsModel temp = new LocationsModel(String.valueOf(new Date().getTime()),
                    currentAddress);
            repo.insertData(temp);
        }
            checkTimestamps();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkTimestamps(){
        int i;
        if(repo.getLocationsDead() != null && repo.getLocationsDead().size() > 0){
            for(i = 0; i < repo.getLocationsDead().size(); i++){
                if ((new Date().getTime() - Long.parseLong(repo.getLocationsDead().get(i).timestamp)
                        > Long.parseLong(sharedPreferences.getString(
                                "deleteInterval", "43200000")))) {
                    repo.deleteLocation(repo.getLocationsDead().get(i));
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,
                NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Aplikacija radi u pozadini")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setSmallIcon(R.drawable.ic_baseline_coronavirus_24)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: SERVICE DESTROYED");
        mTimerToGetLoc.cancel();
        super.onDestroy();
    }
}
