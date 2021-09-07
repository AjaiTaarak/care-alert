package taarak.com.targettrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Main_Location extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    SharedPreferences sp;
    SharedPreferences.Editor edt;

    LocationManager locationManager;
     boolean isContinue = false,isRejected=false;
    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sp=getApplicationContext().getSharedPreferences("Key", Main_Location.MODE_PRIVATE);
        edt=sp.edit();
        locationManager= (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds


        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
            Log.e("GPS",isGPS+" ");
            if(isGPS)
                getLocation();
            else{
                AppConstants.CUR_LAT="1";
                AppConstants.CUR_LONG="1";
                startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
                overridePendingTransition(0,0);
            }

        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            AppConstants.CUR_LAT=wayLatitude+"";
                            AppConstants.CUR_LONG=wayLongitude+"";
                            startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
                            overridePendingTransition(0,0);
                        } else {
                            AppConstants.CUR_LAT=wayLatitude+"";
                            AppConstants.CUR_LONG=wayLongitude+"";
                            startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
                            overridePendingTransition(0,0);
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }else
                    {
                        Log.e("got","4");
                    }
                }
            }
        };




    }


    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(Main_Location.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Main_Location.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Main_Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {

                mFusedLocationClient.getLastLocation().addOnSuccessListener(Main_Location.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        AppConstants.CUR_LAT=wayLatitude+"";
                        AppConstants.CUR_LONG=wayLongitude+"";
                        startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
                        overridePendingTransition(0,0);
                    } else {

                        mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("got",e.getMessage());
                    }
                });

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                Log.e("er","req");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(Main_Location.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                AppConstants.CUR_LAT=wayLatitude+"";
                                AppConstants.CUR_LONG=wayLongitude+"";
                                Log.e("test",wayLatitude+"");
                                //startActivity(new Intent(Location_activity.this,Login.class));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

                        Log.e("er","reject");
                        AppConstants.CUR_LAT="1";
                        AppConstants.CUR_LONG="1";
                        startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
                    overridePendingTransition(0,0);
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                Log.e("er","accept");
                Log.e("GPS",isGPS+" ");

                getLocation();

            }else{
                Log.e("er","reject");
                AppConstants.CUR_LAT="1";
                AppConstants.CUR_LONG="1";
                startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
                overridePendingTransition(0,0);
            }
        }
        else{
            Log.e("er","reject");
            AppConstants.CUR_LAT="1";
            AppConstants.CUR_LONG="1";
            startActivity(new Intent(Main_Location.this, Vehicle_Map.class));
            overridePendingTransition(0,0);
        }
    }
}