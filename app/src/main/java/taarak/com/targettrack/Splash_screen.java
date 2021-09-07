package taarak.com.targettrack;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class Splash_screen extends AppCompatActivity {


    private FrameLayout rootLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);

        setContentView(R.layout.activity_splashscreen);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }


        rootLayout = findViewById(R.id.root_layout);

        rootLayout.setVisibility(View.INVISIBLE);


        new CountDownTimer(1000, 500) {

            @Override
            public void onTick(long millsUntilFinished) {



                    ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                circularRevealActivity();
                                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                FirebaseUser use= FirebaseAuth.getInstance().getCurrentUser();
                if(isNetworkconnected() && use!=null) {
                    SharedPreferences sp=getApplicationContext().getSharedPreferences("Key",Splash_screen.MODE_PRIVATE);
                    SharedPreferences.Editor edt=sp.edit();
                    edt.putString("phone",use.getPhoneNumber());
                    edt.putString("uid",use.getUid());
                    Log.e("phone",use.getPhoneNumber()+"dfv");
                    edt.apply();
                    edt.commit();
                    Intent log = new Intent(Splash_screen.this, MainActivity.class);
                    startActivity(log);
                }else if(use==null){

                    startActivity(new Intent(Splash_screen.this,Login.class));
                    overridePendingTransition(0,0);
                }

            }
        }.start();
    }


    private boolean isNetworkconnected(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null;
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity() {
        int cx = rootLayout.getWidth() / 2;
        int cy = rootLayout.getHeight() / 2;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(2000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }
}
