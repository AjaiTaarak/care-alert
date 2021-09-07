package taarak.com.targettrack;

/**
 * Created by Taarak's Legion on 11-08-2019.
 */

public class Model_location {


    public Float longitude;
    public Float latitude;
    public String time;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Model_location() {
    }

    public Model_location(Float longitude, Float latitude, String time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public Float getLongitude() {
        return longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public String getTime() {
        return time;
    }

}