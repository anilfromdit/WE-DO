package com.afd.wedo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        new Thread(new Runnable() {
            public void run() {
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                try {
                    Thread.sleep(1500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();

                }
    }).start();
}}