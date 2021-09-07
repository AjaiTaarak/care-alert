package taarak.com.targettrack;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor edt;
    Float[] lattitude,longitude;
    String[] timestamp,truck_id,key,phone;
    String str_phone;
    ListView lv_vech;
    Button btn_getloc;
    List<Model_truck> upload_trucks;
    List<Model_user> list_users;
    TextView txt_temp,txt_oxygen,txt_health,txt_hr;
    Boolean gotLocation = false;
    final int NOTIFICATION_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_vech=findViewById(R.id.lv_vech);
        txt_temp = findViewById(R.id.txt_home_temp);
        txt_oxygen = findViewById(R.id.txt_home_oxygen);
        txt_health = findViewById(R.id.txt_home_health);
        txt_hr = findViewById(R.id.txt_home_hr);
        upload_trucks=new ArrayList<>();

        sp=getApplicationContext().getSharedPreferences("Key",MainActivity.MODE_PRIVATE);
        edt=sp.edit();
        str_phone=sp.getString("phone",null);
        btn_getloc=findViewById(R.id.btn_getloc);
        btn_getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Main_Location.class));
                overridePendingTransition(0,0);
            }
        });
        setupHealthListner();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("location");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Model_truck upload = postSnapshot.getValue(Model_truck.class);
                    upload_trucks.add(upload);

                }

                if (upload_trucks.size() != 0) {

                    lattitude=new Float[upload_trucks.size()];
                    longitude=new Float[upload_trucks.size()];
                    timestamp=new String[upload_trucks.size()];
                    truck_id=new String[upload_trucks.size()];
                    key = new String[upload_trucks.size()];
                    phone = new String[upload_trucks.size()];
                    for(int i=0;i<upload_trucks.size();i++) {

                        lattitude[i]=upload_trucks.get(i).getLatitude();
                        longitude[i]=upload_trucks.get(i).getLongitude();
                        timestamp[i]=upload_trucks.get(i).getTime();
                        truck_id[i]=upload_trucks.get(i).getTruck_id();
                        phone[i]=upload_trucks.get(i).getPhone();
                        key[i] = upload_trucks.get(i).getKey();

                    }
                    Adapter_sellerlist adapter_sellerlist=new Adapter_sellerlist(MainActivity.this,upload_trucks.size(),truck_id,timestamp);
                    lv_vech.setAdapter(adapter_sellerlist);
                    lv_vech.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            edt.putString("name",truck_id[i]);
                            edt.putString("phone",phone[i]);
                            edt.putString("time",timestamp[i]);
                            edt.putFloat("latitude",lattitude[i]);
                            edt.putFloat("longitude",longitude[i]);
                            edt.putString("key",key[i]);
                            edt.apply();
                            edt.commit();
                            startActivity(new Intent(MainActivity.this,Vehicle_Map.class));

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "ERROR on cancel"+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    private void setupHealthListner(){
        list_users = new ArrayList<>();
        Log.e("TAG", "setupHealthListner: "+str_phone);
        Query databaseref_healthdata = FirebaseDatabase.getInstance().getReference("userdata").orderByKey().equalTo(str_phone.substring(3));
        databaseref_healthdata.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Model_user upload = postSnapshot.getValue(Model_user.class);
                    list_users.add(upload);
                }
                if(list_users.size() > 0){
                    float tempValue = list_users.get(list_users.size()-1).getTemperature();
                    float oxygenValue = list_users.get(list_users.size()-1).getOxygen();
                    float hrValue = list_users.get(list_users.size()-1).getHeartrate();
                    txt_oxygen.setText(String.valueOf(tempValue));
                    txt_temp.setText(String.valueOf(oxygenValue));
                    txt_hr.setText(String.valueOf(hrValue));

                    String message;
                    if(oxygenValue < 90f || tempValue >= 99f || hrValue > 120){
                        message = "Health condition is abnormal. Contact a doctor !";
                        txt_health.setText(message);
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        String CHANNEL_ID="MYCHANNEL";
                        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
                        PendingIntent pendingIntent1=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
                        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                                .setContentText("Care&Alert")
                                .setContentTitle(message)
                                .setContentIntent(pendingIntent1)
                                .addAction(android.R.drawable.sym_action_chat,"Contact ambulance",pendingIntent1)
                                .setChannelId(CHANNEL_ID)
                                .setSmallIcon(android.R.drawable.sym_action_chat)
                                .build();

                        NotificationManager notificationManager1=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager1.createNotificationChannel(notificationChannel);
                        notificationManager1.notify(1,notification);

                    }else{
                        message = "Health condition is normal";
                        txt_health.setText(message);
                    }
                    // pending inten

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
