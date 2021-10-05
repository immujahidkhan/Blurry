package com.justclack.blurry;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    TextView logo;
    Typeface fonts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        logo = findViewById(R.id.logo);
        fonts = ResourcesCompat.getFont(this, R.font.spectrashell);
        logo.setTypeface(fonts);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}