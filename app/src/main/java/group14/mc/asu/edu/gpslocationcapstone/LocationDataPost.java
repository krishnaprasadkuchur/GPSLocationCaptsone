package group14.mc.asu.edu.gpslocationcapstone;

import android.os.Handler;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by krish on 10/29/2017.
 * Class to handle posting of data to databases
 */


public class LocationDataPost {
    private FirebaseDatabase database;
    private DatabaseReference rootRef;
    private Retrofit retrofit;
    private MongoPostService service;
    private Gson gson;
    Handler dbposthandler;
    String TAG = "LocationDataPost";

    // Create a locationdatapost object getting the reference for firebase db, and retrofit object
    LocationDataPost(){
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("GPSInformation");
        rootRef.keepSynced(true);
        gson = new Gson();
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.POSTURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MongoPostService.class);
    }

    // Method to post location objects to database: Firebase and MongoDB
    public void postToDB(double lat, double lng,String username) {


        // If lat and long is  0.0. no need to post to db, as there is no correct data to post
        if (lat == 0.0 && lng == 0.0) {
            Log.d(TAG, "Lat lng at 0.0");
        }
        else {
            final LocationObject currentloc = new LocationObject(username, lat, lng); // create custome locationobject class
            String key = rootRef.push().getKey();
            final String locMongoObj = gson.toJson(currentloc);
            rootRef.child(key).setValue(currentloc); // posting currentloc object to firebase
            Log.d(TAG, "Posted to firebase with data: " + locMongoObj);

            dbposthandler = new Handler();
            dbposthandler.post(new Runnable() {

                @Override
                public void run() {

                    Call<LocationObject> call = service.postUser(currentloc); // post currentloc to mongodb

                    call.enqueue(new Callback<LocationObject>() {
                        @Override
                        public void onResponse(Call<LocationObject> call, Response<LocationObject> response) {
                            Log.d(TAG, "Posted to MongoDB with data: " + locMongoObj);
                        }

                        @Override
                        public void onFailure(Call<LocationObject> call, Throwable t) {
                            Log.e(TAG, "Unable to post to MongoDB with data: " + locMongoObj);
                        }
                    });
                }

            });
        }
    }

}
