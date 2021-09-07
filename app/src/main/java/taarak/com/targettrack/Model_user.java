package taarak.com.targettrack;

/**
 * Created by Taarak's Legion on 11-08-2019.
 */

public class Model_user {


    public Float temperature;
    public Float oxygen;
    public Float heartrate;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Model_user() {
    }

    public Model_user(Float temperature, Float oxygen,Float heartrate) {
        this.temperature = temperature;
        this.oxygen = oxygen;
        this.heartrate = heartrate;
    }

    public Float getTemperature() {
        return temperature;
    }

    public Float getHeartrate() {
        return heartrate;
    }

    public Float getOxygen() {
        return oxygen;
    }
}