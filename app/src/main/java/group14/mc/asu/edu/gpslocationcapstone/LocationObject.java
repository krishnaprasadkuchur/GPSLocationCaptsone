package group14.mc.asu.edu.gpslocationcapstone;

/**
 * Created by krish on 10/27/2017.
 * POJO class to hold location information, which is posted to firebase and mongoDB
 */


public class LocationObject {
    private double lat,lng;
    private String user;
    private long time;


    public LocationObject() {
    }

    public LocationObject(String name, double latitude, double longitude){
        lat = latitude;
        lng = longitude;
        user = name;
        time = System.currentTimeMillis();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
