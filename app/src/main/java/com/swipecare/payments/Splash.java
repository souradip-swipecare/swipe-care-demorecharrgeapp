package com.swipecare.payments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    TextView textview_name;
    Animation animFadein;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //to hide the title

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
//        getSupportActionBar().hide(); // to the title bar

        textview_name=findViewById(R.id.textview_name);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        textview_name.setVisibility(View.VISIBLE);
        textview_name.startAnimation(animFadein);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharePrfeManager.getInstance(Splash.this).mCheckLogin())
                {
                    startActivity(new Intent(Splash.this,MainActivity.class));
                    finish();
                }
                else
                {
//                    SharePrfeManager.getInstance(Splash.this).mSetBaseUrl("http://login.securepayments.co.in/");
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }
            }
        },3000);
    }
}
