package com.watotoappgmail.watotoapp2;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 7){
            setContentView(R.layout.activity_launcher_nogout);
        } else {
            setContentView(R.layout.activity_launcher);
        }
        getSupportActionBar().hide();

        //Fire an Intent to WelcomeActivity after four (4) seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LauncherActivity.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
}
