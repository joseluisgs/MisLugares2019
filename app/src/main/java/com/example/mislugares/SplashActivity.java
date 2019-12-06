package com.example.mislugares;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //crea un intent para ir al activity main
        intent = new Intent(this,MainActivity.class);

        //un hilo con duración 1'5 s que empieza el intent cuando transcurra el tiempo y  se cierra.
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}
