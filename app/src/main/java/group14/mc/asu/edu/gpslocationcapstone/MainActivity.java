package group14.mc.asu.edu.gpslocationcapstone;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import group14.mc.asu.edu.gpslocationcapstone.LocationFetcherService.LocationServiceBinder;

/**
 * Created by krish on 10/28/2017.
 * Class to handle start, stop, and post of rider information
 */

public class MainActivity extends AppCompatActivity {

    Button startServiceButton, stopServiceButton;
    EditText riderNameText;
    String ridername;
    Context ctx;
    MainActivity mainActivity;
    final String TAG = "MainActivity";
    LocationFetcherService locationFetcherService;
    boolean isServiceConnected = false;
    private ServiceConnection serviceConnection;
    private Thread locationServiceThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        mainActivity = MainActivity.this;
        checkAndRequestPermissions();
        riderNameText = findViewById(R.id.riderNameText);
        startServiceButton = findViewById(R.id.startTrackingButton);
        stopServiceButton = findViewById(R.id.stopTrackingButton);
        stopServiceButton.setEnabled(false);

    }


    // Method to start the background location service
    public void startTrackingService(View view){

        startServiceButton.setEnabled(false);
        stopServiceButton.setEnabled(true);
        Toast.makeText(mainActivity, "Started Location tracking", Toast.LENGTH_LONG).show();
        ridername = riderNameText.getText().toString();

        locationServiceThread = new Thread(){
            public void run(){
                serviceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        Log.w(TAG, "Inside onServiceConnected");
                        LocationServiceBinder locationServiceBinder = (LocationServiceBinder) iBinder;
                        locationFetcherService = locationServiceBinder.getService();
                        isServiceConnected = true;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        Log.w(TAG, "Inside onServiceDisconnected");
                        isServiceConnected = false;
                    }
                };

                Intent intent = new Intent(ctx, LocationFetcherService.class);
                intent.putExtra("username",ridername);
                Log.w(TAG, "Inside createServiceConnectionAndBind. Binding the service");
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                Log.w(TAG, "Inside createServiceConnectionAndBind. Starting the service");
            }
        };
        locationServiceThread.start();

    }

    // Method to unbind and stop the backgroung service
    public void stopTrackingService(View view){

        stopServiceButton.setEnabled(false);
        startServiceButton.setEnabled(true);
        Toast.makeText(mainActivity,"Stopped location tracking",Toast.LENGTH_LONG).show();
        stopService(new Intent(ctx, LocationFetcherService.class));
        unbindService(serviceConnection);
        locationServiceThread.interrupt();

    }




    // Ask for location permission from the user
    public void checkAndRequestPermissions() {
        Log.w("MainActivity", "Inside checkAndRequestPermissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET }, AppConstants.EQUEST_CODE);
            }
        }
    }


    // Check if permission was granted by user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.w("MainActivity", "Inside onRequestPermissionsResult");
        switch (requestCode) {
            case (AppConstants.EQUEST_CODE): if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


}
