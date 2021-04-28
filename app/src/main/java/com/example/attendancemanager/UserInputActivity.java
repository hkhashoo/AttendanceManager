package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeEvent;

public class UserInputActivity extends AppCompatActivity {
    SharedPreferences prefs;
    Intent i;
    EditText name, semester, criteria;
    LinearLayout goButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        name = findViewById(R.id.input_name);
        semester = findViewById(R.id.input_sem);
        criteria = findViewById(R.id.input_criteria);
        goButton = findViewById(R.id.userInput_go_button);

        goButton.setOnClickListener(( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namevalue = name.getText().toString();
                String semvalue = semester.getText().toString();
                String critvalue = criteria.getText().toString();

                if(namevalue.isEmpty() || semvalue.isEmpty() || critvalue.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter the Required Information", Toast.LENGTH_SHORT).show();
                }

                else if( Integer.valueOf(semvalue) < 1 || Integer.valueOf(semvalue) > 10){
                    Toast.makeText(getApplicationContext(), "Please Enter a valid semester", Toast.LENGTH_SHORT).show();
                }

                else if(Integer.valueOf(critvalue)<0 || Integer.valueOf(critvalue)>100){
                    Toast.makeText(getApplicationContext(), "Please Enter a valid criteria (in percentage)", Toast.LENGTH_SHORT).show();
                }

                else{
                    editor.putBoolean("intro_done", true);
                    editor.putString("name", namevalue);
                    editor.putString("semester", semvalue);
                    editor.putString("criteria", critvalue);
                    editor.putInt("global_classes_attended", 0);
                    editor.putInt("global_classes_happened", 0);
                    editor.commit();
                    i = new Intent(UserInputActivity.this, MainAppActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }));
    }
}