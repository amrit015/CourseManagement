package com.example.coursemanagement;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

/**
 * Adding routine and saving on the database
 */
public class AddRoutineActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseHelperSubjects db;
    File folder;
    EditText saturdayRoutine;
    EditText sundayRoutine;
    EditText mondayRoutine;
    EditText tuesdayRoutine;
    EditText fridayRoutine;
    EditText wednesdayRoutine;
    EditText thursdayRoutine;
    String routineToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_add);
        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            folder = new File(Environment.getDataDirectory()
                    + "/CourseApplication/");
        }
        // if phone DOES have sd card
        if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            folder = new File(Environment.getExternalStorageDirectory()
                    + "/CourseApplication/");
        }

        sundayRoutine = (EditText) findViewById(R.id.insert_routine_sunday);
        mondayRoutine = (EditText) findViewById(R.id.insert_routine_monday);
        tuesdayRoutine = (EditText) findViewById(R.id.insert_routine_tuesday);
        wednesdayRoutine = (EditText) findViewById(R.id.insert_routine_wednesday);
        thursdayRoutine = (EditText) findViewById(R.id.insert_routine_thursday);
        fridayRoutine = (EditText) findViewById(R.id.insert_routine_friday);
        saturdayRoutine = (EditText) findViewById(R.id.insert_routine_saturday);

        //getting strings if previously saved before
        String getRoutine = getIntent().getStringExtra("Routine_key");
        if (getRoutine.length() > 0) {
            // getting index to substring the received String
            int a = getRoutine.indexOf("Sunday");
            int b = getRoutine.indexOf("Monday");
            int c = getRoutine.indexOf("Tuesday");
            int d = getRoutine.indexOf("Wednesday");
            int e = getRoutine.indexOf("Thursday");
            int f = getRoutine.indexOf("Friday");
            int g = getRoutine.indexOf("Saturday");

            // substring and displaying on the edittext field
            sundayRoutine.setText(getRoutine.substring(a + 9, b).replace("<br/>", ""));
            mondayRoutine.setText(getRoutine.substring(b + 9, c).replace("<br/>", ""));
            tuesdayRoutine.setText(getRoutine.substring(c + 10, d).replace("<br/>", ""));
            wednesdayRoutine.setText(getRoutine.substring(d + 12, e).replace("<br/>", ""));
            thursdayRoutine.setText(getRoutine.substring(e + 10, f).replace("<br/>", ""));
            fridayRoutine.setText(getRoutine.substring(f + 9, g).replace("<br/>", ""));
            saturdayRoutine.setText(getRoutine.substring(g + 11).replace("<br/>", ""));
        }

        //onclick
        Button addRoutine = (Button) findViewById(R.id.add_routine_button);
        Button cancelRoutine = (Button) findViewById(R.id.cancel_routine_button);
        addRoutine.setOnClickListener(this);
        cancelRoutine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_routine_button:
                AddRoutineToDatabase();
                break;
            case R.id.cancel_routine_button:
                finish();
        }
    }

    //adding routine to database
    private void AddRoutineToDatabase() {
        db = new DatabaseHelperSubjects(folder, getApplicationContext());
        String sunday = sundayRoutine.getText().toString();
        if (sunday.equals("")) {
            sunday = "no classes";
        }
        String monday = mondayRoutine.getText().toString();
        if (monday.equals("")) {
            monday = "no classes";
        }
        String tuesday = tuesdayRoutine.getText().toString();
        if (tuesday.equals("")) {
            tuesday = "no classes";
        }
        String wednesday = wednesdayRoutine.getText().toString();
        if (wednesday.equals("")) {
            wednesday = "no classes";
        }
        String thursday = thursdayRoutine.getText().toString();
        if (thursday.equals("")) {
            thursday = "no classes";
        }
        String friday = fridayRoutine.getText().toString();
        if (friday.equals("")) {
            friday = "no classes";
        }
        String saturday = saturdayRoutine.getText().toString();
        if (saturday.equals("")) {
            saturday = "no classes";
        }

        //getting subject Name from the previous activity
        String subject = getIntent().getStringExtra("SubjectName_key");
        Log.i("Add Routine", "Subject : " + subject);
        routineToAdd = "Sunday : " + sunday + "<br/>" + "Monday : " + monday + "<br/>" + "Tuesday : " + tuesday + "<br/>" + "Wednesday : "
                + wednesday + "<br/>" + "Thursday : " + thursday + "<br/>" + "Friday : " + friday + "<br/>" + "Saturday : " + saturday;
        SubjectModule subjectModule = new SubjectModule();
        subjectModule.subjectRoutine = routineToAdd;
        subjectModule.setSubjectRoutine(routineToAdd);
        //updating the routine to the database
        db.updateRoutine(subjectModule, subject);
        finish();
    }
}
