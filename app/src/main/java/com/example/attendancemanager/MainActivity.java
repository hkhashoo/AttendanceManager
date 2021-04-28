package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    LinearLayout go_button;
    SharedPreferences prefs;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        go_button = findViewById(R.id.intro_go_button);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean intro_done = prefs.getBoolean("intro_done", false);

        if(intro_done) {
            i = new Intent(MainActivity.this, MainAppActivity.class); //To be edited.
            startActivity(i);
            finish();
        }

        go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = i = new Intent(MainActivity.this, UserInputActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}