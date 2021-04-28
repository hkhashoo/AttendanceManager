package com.example.attendancemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;

public class MainAppActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout container;
    ImageView button_add, button_editPrefs;
    Dialog popupDialog;
    SharedPreferences prefs;
    DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        Log.d("Main App Activity : ", "OnCreateCalled");
        popupDialog = new Dialog(this);
        container = findViewById(R.id.main_app_container);
        button_add = findViewById(R.id.main_app_add);
        button_add.setOnClickListener(this);
        button_editPrefs = findViewById(R.id.main_app_edit_preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if(savedInstanceState == null) {
            //Initialisation calls
            initialiseThis(editor);
            refreshPanel();
        }
        //--------------------
        button_editPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView button_close;
                Button button_commit;
                EditText edit_name, edit_sem, edit_criteria;
                popupDialog.setContentView(R.layout.global_edit_popup);
                button_close = popupDialog.findViewById(R.id.global_edit_close);
                button_commit = popupDialog.findViewById(R.id.global_edit_commit);
                edit_name = popupDialog.findViewById(R.id.input_name);
                edit_criteria = popupDialog.findViewById(R.id.input_criteria);
                edit_sem = popupDialog.findViewById(R.id.input_sem);
                edit_criteria.setText(prefs.getString("criteria", ""));
                edit_name.setText(prefs.getString("name", ""));
                edit_sem.setText(prefs.getString("semester", ""));

                button_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupDialog.dismiss();
                    }
                });

                button_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String namevalue = edit_name.getText().toString();
                        String semvalue = edit_sem.getText().toString();
                        String critvalue = edit_criteria.getText().toString();

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
                            Toast.makeText(getApplicationContext(), "Changes Applied", Toast.LENGTH_SHORT).show();
                            editor.putString("name", namevalue);
                            editor.putString("semester", semvalue);
                            editor.putString("criteria", critvalue);
                            editor.commit();
                            initialiseThis(editor);
                            refreshPanel();
                            popupDialog.dismiss();
                        }
                    }
                });
                popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupDialog.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        popupDialog.setContentView(R.layout.add_subject_popup);
        ImageView button_close;
        Button button_commit;
        EditText edit_name, edit_ac, edit_tc;
        button_close = popupDialog.findViewById(R.id.subject_add_close);
        button_commit = popupDialog.findViewById(R.id.subject_add_commit);
        edit_name = popupDialog.findViewById(R.id.subject_add_name);
        edit_tc = popupDialog.findViewById(R.id.local_total_classes);
        edit_ac = popupDialog.findViewById(R.id.local_attended_classes);

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss();
            }
        });

        button_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, tc, ac;
                name = edit_name.getText().toString();
                tc = edit_tc.getText().toString();
                ac = edit_ac.getText().toString();

                if(name.isEmpty() || tc.isEmpty() || ac.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter Required Information", Toast.LENGTH_SHORT).show();
                }

                else if (Integer.valueOf(tc) < Integer.valueOf(ac)){
                    Toast.makeText(getApplicationContext(), "Total Classes cannot be less than attended classes", Toast.LENGTH_SHORT).show();
                }

                else if(helper.subjectIsInDB(name)){
                    Toast.makeText(getApplicationContext(), "Subject already exists!", Toast.LENGTH_SHORT).show();
                }

                else{
                    popupDialog.dismiss();
                    addView(name, ac, tc);
                }


            }

        });
        popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupDialog.show();
    }

    private void addView(String subject_name, String attended_classes, String total_classes) {

        View newSubjectRow = getLayoutInflater().inflate(R.layout.subject_element, null, false);
        TextView subjectName, attendedClasses, totalClasses, percentage, comment;
        ImageView add, subtract, edit, delete;

        //Do database stuff here
        if(!helper.subjectIsInDB(subject_name))
            helper.insert(subject_name,Integer.valueOf(attended_classes), Integer.valueOf(total_classes));
        //

        //These are all the elements in the main app activity that need to be added to the view.
        subjectName = newSubjectRow.findViewById(R.id.subjectName);
        attendedClasses = newSubjectRow.findViewById(R.id.local_attended_classes);
        totalClasses = newSubjectRow.findViewById(R.id.local_total_classes);
        percentage = newSubjectRow.findViewById(R.id.local_percentage);
        add = newSubjectRow.findViewById(R.id.button_increase);
        subtract = newSubjectRow.findViewById(R.id.button_decrease);
        edit = newSubjectRow.findViewById(R.id.button_edit);
        comment = newSubjectRow.findViewById(R.id.subject_comment);
        delete = newSubjectRow.findViewById(R.id.button_delete);
        //--------------------------------------------------------------------------------------

        // Setting the views text---------------------------------------------------------------
        subjectName.setText(subject_name);
        attendedClasses.setText(attended_classes);
        totalClasses.setText(total_classes);
        comment.setText(getComment(newSubjectRow, Integer.valueOf(attended_classes), Integer.valueOf(total_classes)));


        if (Integer.valueOf(total_classes) == 0) {
            percentage.setText("0%");

        }

        else {
            int aclasses = Integer.valueOf(attended_classes);
            int tclasses = Integer.valueOf(total_classes);
            percentage.setText(String.format("%.2f", (double)aclasses*100/tclasses)+"%");
        }
        //--------------------------------------------------------------------------------------
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("global_classes_attended", prefs.getInt("global_classes_attended", 0)+ Integer.valueOf(attended_classes));
        editor.putInt("global_classes_happened", prefs.getInt("global_classes_happened", 0) + Integer.valueOf(total_classes));
        editor.commit();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to be changed later
                Integer totalnew = ((Integer.valueOf(totalClasses.getText().toString()) + 1));
                Integer attnew = ((Integer.valueOf(attendedClasses.getText().toString()) + 1));
                attendedClasses.setText(attnew+ "");
                totalClasses.setText( totalnew+ "");
                comment.setText(getComment(newSubjectRow, attnew, totalnew));
                percentage.setText(String.format("%.2f", (double)attnew*100/totalnew)+"%");
                editor.putInt("global_classes_attended", prefs.getInt("global_classes_attended", 0)+ 1);
                editor.putInt("global_classes_happened", prefs.getInt("global_classes_happened", 0) + 1);
                editor.commit();
                helper.changeAttendedTotal(subject_name, String.valueOf(attnew), String.valueOf(totalnew));
                refreshPanel();
            }
        });

        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tnew = ((Integer.valueOf(totalClasses.getText().toString()) + 1));
                Integer anew = Integer.valueOf(attendedClasses.getText().toString());
                totalClasses.setText( tnew+ "");
                comment.setText(getComment(newSubjectRow, anew, tnew));
                percentage.setText(String.format("%.2f", (double)anew*100/tnew)+"%");
                editor.putInt("global_classes_happened", prefs.getInt("global_classes_happened", 0) + 1);
                editor.commit();
                helper.changeAttendedTotal(subject_name, String.valueOf(anew), String.valueOf(tnew));
                refreshPanel();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                int attvalue = prefs.getInt("global_classes_attended", 0);
                int totvalue = prefs.getInt("global_classes_happened", 0);
                attvalue -= Integer.valueOf(attendedClasses.getText().toString());
                totvalue -= Integer.valueOf(totalClasses.getText().toString());
                editor.putInt("global_classes_attended", attvalue);
                editor.putInt("global_classes_happened", totvalue);
                editor.commit();
                helper.removeRow(subjectName.getText().toString());
                container.removeView(newSubjectRow);
                refreshPanel();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.setContentView(R.layout.add_subject_popup);
                TextView title, oldname, oldatt, oldtot;
                ImageView button_close;
                Button button_commit;
                EditText edit_name, edit_ac, edit_tc;
                title = popupDialog.findViewById(R.id.popup_title_add_subject);
                button_close = popupDialog.findViewById(R.id.subject_add_close);
                button_commit = popupDialog.findViewById(R.id.subject_add_commit);
                edit_name = popupDialog.findViewById(R.id.subject_add_name);
                edit_tc = popupDialog.findViewById(R.id.local_total_classes);
                edit_ac = popupDialog.findViewById(R.id.local_attended_classes);
                oldname = newSubjectRow.findViewById(R.id.subjectName);
                oldatt = newSubjectRow.findViewById(R.id.local_attended_classes);
                oldtot = newSubjectRow.findViewById(R.id.local_total_classes);
                edit_name.setText(oldname.getText().toString());
                edit_tc.setText(oldtot.getText().toString());
                edit_ac.setText(oldatt.getText().toString());
                title.setText("Edit Subject");
                button_commit.setText("Ok");

                button_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupDialog.dismiss();
                    }
                });

                button_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name, tc, ac;
                        name = edit_name.getText().toString();
                        tc = edit_tc.getText().toString();
                        ac = edit_ac.getText().toString();

                        if(name.isEmpty() || tc.isEmpty() || ac.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Please Enter Required Information", Toast.LENGTH_SHORT).show();
                        }

                        else if (Integer.valueOf(tc) < Integer.valueOf(ac)){
                            Toast.makeText(getApplicationContext(), "Total Classes cannot be less than attended classes", Toast.LENGTH_SHORT).show();
                        }

                        else if (helper.subjectIsInDB(name) && !(name.equals(oldname.getText().toString())))
                        {
                            Log.d(name, oldname.getText().toString());
                            Toast.makeText(getApplicationContext(), "Subject already exists! ", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            popupDialog.dismiss();
                            editView(newSubjectRow, name, ac,tc);
                        }
                    }

                });
                popupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupDialog.show();
            }
        });
        refreshPanel();
        container.addView(newSubjectRow);
    }

    private void editView(View subjectRow, String name, String attended_classes, String total_classes){
        TextView subName, attendedClasses, totalClasses, percentage, comment;
        subName = subjectRow.findViewById(R.id.subjectName);
        attendedClasses = subjectRow.findViewById(R.id.local_attended_classes);
        totalClasses = subjectRow.findViewById(R.id.local_total_classes);
        percentage =subjectRow.findViewById(R.id.local_percentage);
        comment = subjectRow.findViewById(R.id.subject_comment);
        String oldname = subName.getText().toString();
        subName.setText(name);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("global_classes_attended", prefs.getInt("global_classes_attended", 0)- Integer.valueOf(attendedClasses.getText().toString()));
        editor.putInt("global_classes_happened", prefs.getInt("global_classes_happened", 0)- Integer.valueOf(totalClasses.getText().toString()));
        editor.commit();
        attendedClasses.setText(attended_classes);
        totalClasses.setText(total_classes);
        editor.putInt("global_classes_attended", prefs.getInt("global_classes_attended", 0)+ Integer.valueOf(attended_classes));
        editor.putInt("global_classes_happened", prefs.getInt("global_classes_happened", 0)+ Integer.valueOf(total_classes));
        editor.commit();
        if (Integer.valueOf(total_classes) == 0) {
            percentage.setText("0%");
        }

        else {
            int aclasses = Integer.valueOf(attended_classes);
            int tclasses = Integer.valueOf(total_classes);
            percentage.setText(String.format("%.2f", (double)aclasses*100/tclasses)+"%");

        }
        if(helper.subjectIsInDB(name))
            helper.changeAttendedTotal(name, attended_classes, total_classes);
        else{
            helper.changeAttendedTotal(name, attended_classes,total_classes);
            helper.changeName(oldname, name);
        }
        comment.setText(getComment(subjectRow, Integer.valueOf(attended_classes), Integer.valueOf(total_classes)));
        refreshPanel();
    }

    private void refreshPanel(){
        String username = "Welcome, "+ prefs.getString("name", "").split(" ")[0];
        String criteria = "Current Goal "+ prefs.getString("criteria", "") + "%";
        Integer attClasses = prefs.getInt("global_classes_attended", 0);
        Integer totalClasses = prefs.getInt("global_classes_happened", 0);
        String percentage;
        if(totalClasses != 0)
            percentage =  String.format("%.2f",(double)attClasses.intValue()*100/totalClasses.intValue());
        else
            percentage = "0";
        TextView name, crit, ac, tc, per;
        name = findViewById(R.id.main_app_name);
        crit = findViewById(R.id.main_app_goal_attendance);
        ac = findViewById(R.id.main_app_attendance_current);
        tc = findViewById(R.id.main_app_total_classes);
        per = findViewById(R.id.main_app_percentage);

        name.setText(username);
        crit.setText(criteria);
        ac.setText(attClasses+"");
        tc.setText(totalClasses+"");
        per.setText(percentage+"%");


    }

    private String getComment(View c, int attendedClasses, int totalClasses){
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_matchelement);
        c.setBackground(drawable);
        if(attendedClasses == 0) return "Let the classes begin!";

        double Criteria = Double.valueOf(prefs.getString("criteria", "0.0"));
        Log.d("Hello ", Criteria+"");
        double TotalMet = (double)attendedClasses*100/totalClasses;

        if(Criteria > TotalMet){
            drawable = ContextCompat.getDrawable(this, R.drawable.bg_matchalert);
            c.setBackground(drawable);
            int i=0;
            while(Criteria > TotalMet){
                i+=1;
                attendedClasses +=1;
                totalClasses+=1;
                Log.d("Hey there", TotalMet+"");
                TotalMet = (double)attendedClasses*100/totalClasses;


            }
            if(i== 1) return "Attend 1 more class to get back on track";
            else return "Attend " + i+ " more classes to get back on track";
        }

        else {
            int i=0;
            while(Criteria < TotalMet){
                i+=1;
                totalClasses +=1;
                TotalMet = attendedClasses*100/totalClasses;
                Log.d("Hey there", TotalMet+"");
            }

            if(i <= 1) return "On track. You cant miss the next class";

            else if (i==2) return "On track. You can miss the next class";

            else return "On track. You can miss the next " + (i-1) + " classes";
        }
    }

    private void initialiseThis(SharedPreferences.Editor editor){
        editor.putInt("global_classes_attended", 0);
        editor.putInt("global_classes_happened", 0);
        editor.commit();
        container = findViewById(R.id.main_app_container);
        container.removeAllViews();
        if (helper.tableIsNotEmpty()) {
            Cursor cursor = helper.getAllData();
            while (cursor.moveToNext()) {
                int attended_classes = cursor.getInt(2);
                int total_classes = cursor.getInt(3);
                addView(cursor.getString(1), String.valueOf(attended_classes), String.valueOf(total_classes));
            }
        }
    }



}