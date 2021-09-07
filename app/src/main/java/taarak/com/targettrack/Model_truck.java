package taarak.com.targettrack;

/**
 * Created by Taarak's Legion on 11-08-2019.
 */

public class Model_truck {


    public Float longitude;
    public Float latitude;
    public String time;
    public String truck_id;
    public String key;
    public String phone;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Model_truck() {
    }

    public Model_truck(Float longitude, Float latitude, String time, String truck_id, String key, String phone) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.truck_id = truck_id;
        this.key = key;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
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

    public String getTruck_id() {
        return truck_id;
    }

    public String getKey() {
        return key;
    }
}