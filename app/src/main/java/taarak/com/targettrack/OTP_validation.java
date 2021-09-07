package taarak.com.targettrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OTP_validation extends AppCompatActivity {

    public static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    private static final String TAG = "PhoneAuthActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    Boolean code_sent=false,isfromLogin=false;

    EditText edt_otp;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    String phone;
    Button btn_otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_validation);

        sp=getApplicationContext().getSharedPreferences("Key",OTP_validation.MODE_PRIVATE);
        edt=sp.edit();
        btn_otp=findViewById(R.id.btn_reg_otp1);
        edt_otp=findViewById(R.id.edt_reg_otp1);
        isfromLogin=sp.getBoolean("login",false);
        if(!isfromLogin) {
            phone = sp.getString("reg_phone", null);

        }
        else {
            phone = sp.getString("log_phone", null);
        }

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(code_sent)
                {
                    String code=edt_otp.getText().toString();
                    //verifyPhoneNumberWithCode(phone,code);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
                if (!validatePhoneNumber()) {
                    Snackbar.make(v,"Not a Valid Number", Snackbar.LENGTH_LONG).setAction("Action",null).show();

                }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.e(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                code_sent=false;
                //startActivity(new Intent(Login.this,MainActivity.class));
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.e(TAG, "onVerificationFailed", e);

                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(OTP_validation.this,"Invalid Number", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }


            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.e(TAG, "onCodeSent:" + verificationId);

                code_sent=true;
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };
        startPhoneNumberVerification("+91"+phone);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OTP_validation.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        Log.e("CREDE",credential.getSmsCode()+" ");
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            if(!isfromLogin)
                            OnSignedIn(user.getPhoneNumber());
                            else
                                checkmail();

                            startActivity(new Intent(OTP_validation.this,MainActivity.class));

                        } else {

                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {


                            }

                        }
                    }
                });
    }



    public void checkmail(){
        FirebaseUser use= FirebaseAuth.getInstance().getCurrentUser();

        edt.putString("phone",use.getPhoneNumber());
        edt.putString("uid",use.getUid());
        edt.apply();
        edt.commit();

       // startActivity(new Intent(OTP_validation.this,Main_page.class));

    }
    private boolean validatePhoneNumber() {
        String phoneNumber = phone;
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(OTP_validation.this,"Invalid Number", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    protected void OnSignedIn(String phone) {

        SharedPreferences p=getApplicationContext().getSharedPreferences("Key", OTP_validation.MODE_PRIVATE);
        SharedPreferences.Editor edt=p.edit();


        try {
            FirebaseUser use= FirebaseAuth.getInstance().getCurrentUser();
            edt.putString("phone",phone);
            edt.putString("uid",use.getUid());
            edt.apply();
            edt.commit();

            startActivity(new Intent(OTP_validation.this,MainActivity.class));

        }
        catch (Exception e)
        {
            Log.e("err",e.getMessage());
        }


    }

}
