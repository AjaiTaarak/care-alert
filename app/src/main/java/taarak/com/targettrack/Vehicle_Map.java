package taarak.com.targettrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class Vehicle_Map extends AppCompatActivity implements OnMapReadyCallback {

    double latitude,longitude,ulat,ulong;
    SupportMapFragment mapFragment;
    SharedPreferences sp;
    Geocoder geocoder;
    String ambulance_id;
    List<Address> addresses;
    List<Model_truck> list_locationdata;
    TextView txt_addr,txt_dist,txt_name,txt_phone;
    GoogleMap mainMap;
    WebView wb;
    ImageView img_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals__map);
        sp=getApplicationContext().getSharedPreferences("Key",Vehicle_Map.MODE_PRIVATE);
        txt_addr=findViewById(R.id.trk_addr);
        txt_dist=findViewById(R.id.txt_dist);
        txt_name=findViewById(R.id.trk_name);
        txt_phone = findViewById(R.id.trk_phone);
        img_location = findViewById(R.id.img_getuserlocation);
        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Main_Location.class));
                overridePendingTransition(0,0);
            }
        });
        txt_name.setText("Name :"+sp.getString("name","Test"));
        txt_phone.setText("Phone :"+sp.getString("phone","Test"));
        txt_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+91"+sp.getString("phone","Test")));
                startActivity(intent);
            }
        });
        String temp[]=sp.getString("latlong","11.0-76.0").split("-");

        latitude= sp.getFloat("latitude",11.0f);
        longitude= sp.getFloat("longitude",76.0f);
        ambulance_id = sp.getString("key",null);

        list_locationdata = new ArrayList<>();
        ulat= Double.parseDouble(AppConstants.CUR_LAT);
        ulong= Double.parseDouble(AppConstants.CUR_LONG);
        Double res=distance(latitude,longitude,ulat,ulong);
        txt_dist.setText("Distance "+res);

        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            txt_addr.setText(addresses.get(0).getAddressLine(0)+" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        setupHealthListner(ambulance_id);
    }
    private void setupHealthListner(String key){
        DatabaseReference databaseref_ambulancedata = FirebaseDatabase.getInstance().getReference("location");
        databaseref_ambulancedata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Model_truck upload = postSnapshot.getValue(Model_truck.class);
                    list_locationdata.add(upload);
                }
                if(list_locationdata.size() > 0){
                    Log.e("TAG", "onDataChange:     ");
                    latitude = list_locationdata.get(list_locationdata.size()-1).getLatitude();
                    longitude = list_locationdata.get(list_locationdata.size()-1).getLongitude();
                    LatLng ambulance = new LatLng(latitude,longitude);
                    Log.e("TAG", "onDataChange: "+latitude+" "+longitude );
                    setupMarkers(new LatLng(ulat,ulong),ambulance);
                    Double res=distance(latitude,longitude,ulat,ulong);
                    txt_dist.setText("Distance "+res);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static double distance(double lat1, double lon1,
                                  double lat2, double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng user = new LatLng(latitude,longitude);
        LatLng truck = new LatLng(ulat,ulong);
        mainMap = googleMap;
        setupMarkers(user,truck);
    }
    public void setupMarkers(LatLng user,LatLng ambulance){

        mainMap.clear();
        mainMap.addPolyline(new PolylineOptions().add(user,ambulance));
        mainMap.addMarker(new MarkerOptions().position(user)
                .title("Admin"));
        mainMap.addMarker(new MarkerOptions().position(ambulance)
                .title("Truck"));
        mainMap.moveCamera(CameraUpdateFactory.newLatLng(ambulance));
//        mainMap.animateCamera(CameraUpdateFactory.zoomIn());
        mainMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }
}
