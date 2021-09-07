package taarak.com.targettrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText edt_phone;
    TextView txt_mailinvalid;
    Button btn_login;
    String phone,res_Pass=null,str_uid;
    SharedPreferences sp;
    ProgressBar prg_login;
    SharedPreferences.Editor edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initviews();
        sp=getApplicationContext().getSharedPreferences("Key", Login.MODE_PRIVATE);
        edt=sp.edit();

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prg_login.setVisibility(View.VISIBLE);
                 phone=edt_phone.getText().toString();
                try {
                    if (isNullOrEmpty(phone) && phone.length()==10) {
                        edt.putString("log_phone",phone);
                        edt.putBoolean("login",true);
                        edt.apply();
                        edt.commit();
                        startActivity(new Intent(Login.this,OTP_validation.class));
                        overridePendingTransition(0,0);
                    }
                    else {
                        txt_mailinvalid.setVisibility(View.VISIBLE);
                        prg_login.setVisibility(View.GONE);
                    }
                }catch (Exception e)
                {

                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }




    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return true;
        return false;
    }
    public void initviews(){

        edt_phone =findViewById(R.id.txt_login_phone);
//        edt_pass=findViewById(R.id.txt_login_password);
  //      txt_passinvalid=findViewById(R.id.txt_login_passinvalid);
        prg_login=findViewById(R.id.spn_login);
        txt_mailinvalid=findViewById(R.id.txt_login_mailinvalidd);
        btn_login=findViewById(R.id.btn_login_submit);
    }
//    protected void OnSignedIn(String phone) {
//
//        SharedPreferences p = getApplicationContext().getSharedPreferences("Key", Registrationmain.MODE_PRIVATE);
//        SharedPreferences.Editor edt = p.edit();
//        edt.putString("phone", phone);
//        edt.putString("uid", str_uid);
//        edt.apply();
//        edt.commit();
//    }
}
