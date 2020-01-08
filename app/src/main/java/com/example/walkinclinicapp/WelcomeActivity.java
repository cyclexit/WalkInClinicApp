package com.example.walkinclinicapp;

import java.util.Timer;
import android.os.Bundle;
import java.util.TimerTask;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        final Intent intent = new Intent(this, MainActivity.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(task, 500);
    }
}
