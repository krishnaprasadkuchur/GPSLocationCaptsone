package group14.mc.asu.edu.gpslocationcapstone;

/**
 * Created by krish on 10/29/2017.
 * Service class to get latest latitude and longitude
 */


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class LocationFetcherService extends Service {

    private final IBinder locationServiceBinder = new LocationServiceBinder();
    LocationManager locationManager;
    LocationListener locationListener;
    private double latestLattitude;
    private double latestLongitude;
    private Handler handler;
    private Runnable runnable;
    String username;
    LocationDataPost locationDataPost;
    final String TAG = "LocationFetcherService";


    @Override
    public IBinder onBind(Intent intent) {
      // Bind service to activity
        username = intent.getStringExtra("username");
        Log.d(TAG,"Username is: "+username);
        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                try{

                    Log.d(TAG, "Saving for user:"+username);
                    getLocation(); // Method to get latest lat and long coordinates

                }
                catch (Exception e) {
                    Log.e(TAG, "Exception while getting and storing location");
                }
                finally{

                    handler.postDelayed(this, 60000); //Get loc and lat values every minute
                }
            }
        };
        handler.post(runnable);
        return locationServiceBinder;
    }

    public class LocationServiceBinder extends Binder {
        LocationFetcherService getService() {
            return LocationFetcherService.this;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "Service Started-------------");

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.w(TAG, "Inside startLocationService");
        locationDataPost = new LocationDataPost();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.w(TAG, "from service");
                latestLongitude = location.getLongitude();
                latestLattitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Log.w(TAG, "Inside onProviderDisabled");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        locationManager.requestLocationUpdates("gps", 1000,0,locationListener); // Get location updates
    }

    public void getLocation() {

        if(username.equals(null)) username = "";
        Log.d(TAG,"Inside getLocation"+latestLattitude+"and "+latestLongitude);
        locationDataPost.postToDB(latestLattitude, latestLongitude, username); // send lat, long to LocationDatapost for insertion to db
    }


    @Override
    public void onDestroy() {
        locationDataPost.postToDB(latestLattitude,latestLongitude, username);
        handler.removeCallbacksAndMessages(null);
        Log.w(TAG, "Service STOPPED----------------");
        locationManager.removeUpdates(locationListener);
    }

}
