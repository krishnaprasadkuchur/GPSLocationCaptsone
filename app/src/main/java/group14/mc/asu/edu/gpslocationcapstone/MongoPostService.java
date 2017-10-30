package group14.mc.asu.edu.gpslocationcapstone;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by krish on 10/28/2017.
 * Retrofit2 Interface to post the location object to the MongoDB on server
 */

public interface MongoPostService {

    @POST("/")
   Call<LocationObject> postUser(@Body LocationObject locationObject);

}
