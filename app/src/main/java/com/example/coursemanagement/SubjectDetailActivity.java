package com.example.coursemanagement;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is an activity java class which shows the routine, notes, deadline of the respective subject on cardview.
 * Also operations to upload, set reminder are implemented here
 */
public class SubjectDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SubjectDetailActivity";
    ImageView routineAdd, notesAdd, deadlineAdd;
    LinearLayout layoutRoutine, layoutNotes, layoutDeadline;
    TextView routineUpdater, notesUpdater, deadlineUpdater;
    SubjectModule subjectModule;
    String subjectName, subjectUpdater, subjectNotes, subjectDeadline, subjectRoutine, showLayout, identifier;
    TextView routineShow, deadlineShow, notesShow;
    DatabaseHelperSubjects db;
    File folder;
    ImageView optionsRoutine, optionsNotes, optionsDeadline;
    Dialog dialogRemainder;
    DatePickerDialog toDatePickerDialog;
    DatabaseReminder databaseReminder;
    String dateToReturn;
    String requiredDate;
    String timeHere;
    String timeToReturn;
    LinearLayout layoutDate;
    LinearLayout layoutTime;
    TextView time;
    TextView date;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_detail_subject);
        //getting bundle passed from the fragments
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            subjectModule = bundle.getParcelable("Subject_key");
            subjectName = subjectModule.getSubjectName();
            subjectUpdater = subjectModule.getSubjectUpdater();
            subjectNotes = subjectModule.getSubjectNotes();
            subjectDeadline = subjectModule.getSubjectDeadline();
            subjectRoutine = subjectModule.getSubjectRoutine();
            showLayout = subjectModule.getShowLayout();
            identifier = subjectModule.getIdentifier();
            Log.i(TAG, "subject name : " + subjectName);
            Log.i(TAG, "subject user : " + subjectUpdater);
            setTitle(subjectName);
        }

        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            folder = new File(Environment.getDataDirectory()
                    + "/CourseApplication/");
            Log.i(TAG, "folder : " + folder);
        }
        // if phone DOES have sd card
        if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            folder = new File(Environment.getExternalStorageDirectory()
                    + "/CourseApplication/");
            Log.i(TAG, "folder : " + folder);
        }
        //initializing database
        db = new DatabaseHelperSubjects(folder, getApplicationContext());

        routineUpdater = (TextView) findViewById(R.id.subject_routine_update);
        notesUpdater = (TextView) findViewById(R.id.subject_notes_update);
        deadlineUpdater = (TextView) findViewById(R.id.subject_deadline_update);

        layoutRoutine = (LinearLayout) findViewById(R.id.layout_routine);
        layoutNotes = (LinearLayout) findViewById(R.id.layout_notes);
        layoutDeadline = (LinearLayout) findViewById(R.id.layout_deadline);
        routineAdd = (ImageView) findViewById(R.id.subject_routine_add);
        notesAdd = (ImageView) findViewById(R.id.subject_notes_add);
        deadlineAdd = (ImageView) findViewById(R.id.subject_deadline_add);
        routineShow = (TextView) findViewById(R.id.show_routine);
        deadlineShow = (TextView) findViewById(R.id.deadline_show);
        notesShow = (TextView) findViewById(R.id.notes_show);
        routineAdd.setOnClickListener(this);
        notesAdd.setOnClickListener(this);
        deadlineAdd.setOnClickListener(this);

        optionsDeadline = (ImageView) findViewById(R.id.options_deadline);
        optionsNotes = (ImageView) findViewById(R.id.options_notes);
        optionsRoutine = (ImageView) findViewById(R.id.options_routine);

        // on-click listener
        optionsRoutine.setOnClickListener(this);
        optionsNotes.setOnClickListener(this);
        optionsDeadline.setOnClickListener(this);

//        //deciding when to show the add sign (Add sign only on local files)
        if (showLayout.equals("true")) {
            // if the taken strings contains some value, showing these values
            if (!subjectRoutine.equals("")) {
                routineShow.setText(Html.fromHtml(subjectRoutine));
                routineAdd.setVisibility(View.GONE);
            } else {
                layoutRoutine.setVisibility(View.GONE);
            }
            if (!subjectDeadline.equals("")) {
                deadlineShow.setText(subjectDeadline);
                deadlineAdd.setVisibility(View.GONE);
            } else {
                layoutDeadline.setVisibility(View.GONE);
            }
            if (!subjectNotes.equals("")) {
                notesShow.setText(subjectNotes);
                notesAdd.setVisibility(View.GONE);
            } else {
                layoutNotes.setVisibility(View.GONE);
            }
        }

        // not showing + sign on Home Fragment subjects
        if (showLayout.equals("false")) {
            // removing the + sign from layout by setting visibility to GONE
            routineAdd.setVisibility(View.GONE);
            notesAdd.setVisibility(View.GONE);
            deadlineAdd.setVisibility(View.GONE);
            // removing the cardview options from layout by setting visibility to GONE
            optionsDeadline.setVisibility(View.GONE);
            optionsNotes.setVisibility(View.GONE);
            optionsRoutine.setVisibility(View.GONE);

            // if the taken strings contains some value, showing these values
            if (!subjectRoutine.equals("")) {
                routineShow.setText(Html.fromHtml(subjectRoutine));
            } else {
                layoutRoutine.setVisibility(View.GONE);
            }
            if (!subjectDeadline.equals("")) {
                deadlineShow.setText(subjectDeadline);
            } else {
                layoutDeadline.setVisibility(View.GONE);
            }
            if (!subjectNotes.equals("")) {
                notesShow.setText(subjectNotes);
            } else {
                layoutNotes.setVisibility(View.GONE);
            }
        }

        routineUpdater.setText("Updated by " + subjectUpdater);
        notesUpdater.setText("Updated by " + subjectUpdater);
        deadlineUpdater.setText("Updated by " + subjectUpdater);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subject_routine_add:
                addRoutine(subjectRoutine);
                break;
            case R.id.subject_notes_add:
                addNotes(subjectNotes);
                break;
            case R.id.subject_deadline_add:
                addDeadline(subjectDeadline);
                break;
            case R.id.options_deadline:
                // options only on Local Fragment subjects
                if (showLayout.equals("true"))
                    addDeadline(subjectDeadline);
                break;
            case R.id.options_notes:
                // options only on Local Fragment subjects
                if (showLayout.equals("true"))
                    addNotes(subjectNotes);
                break;
            case R.id.options_routine:
                // options only on Local Fragment subjects
                if (showLayout.equals("true"))
                    addRoutine(subjectRoutine);
                break;
            default:
                break;
        }
        if (v == layoutDate)
            toDatePickerDialog.show();
    }

    //adding deadline
    private void addDeadline(String subjectDeadline) {
        final Dialog deadlineDialog = new Dialog(this);
        deadlineDialog.setContentView(R.layout.deadline_add_layout);
        final EditText insertDeadline = (EditText) deadlineDialog.findViewById(R.id.insert_deadline);
        if (!subjectDeadline.equals("")) {
            insertDeadline.setText(subjectDeadline);
        }
        final Button addDeadline = (Button) deadlineDialog.findViewById(R.id.add_deadline_button);
        Button cancelDeadline = (Button) deadlineDialog.findViewById(R.id.cancel_deadline_button);
        addDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deadline = insertDeadline.getText().toString();
                SubjectModule subjectModule = new SubjectModule();
                subjectModule.subjectDeadline = deadline;
                db.updateDeadline(subjectModule, subjectName);
                Toast.makeText(getApplicationContext(), "Successfully updated Deadline", Toast.LENGTH_SHORT).show();
                deadlineDialog.dismiss();
                deadlineShow.setText(deadline);
                subjectModule.setSubjectDeadline(deadline);
                deadlineAdd.setVisibility(View.GONE);
                layoutDeadline.setVisibility(View.VISIBLE);
            }
        });
        cancelDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadlineDialog.dismiss();
            }
        });
        deadlineDialog.show();
    }

    //adding notes
    private void addNotes(String subjectNotes) {
        final Dialog notesDialog = new Dialog(this);
        notesDialog.setContentView(R.layout.notes_add_layout);
        final EditText insertNotes = (EditText) notesDialog.findViewById(R.id.insert_notes);
        if (!subjectNotes.equals("")) {
            insertNotes.setText(subjectNotes);
        }
        final Button addNotes = (Button) notesDialog.findViewById(R.id.add_notes_button);
        Button cancelNotes = (Button) notesDialog.findViewById(R.id.cancel_notes_button);
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = insertNotes.getText().toString();
                SubjectModule subjectModule = new SubjectModule();
                subjectModule.subjectNotes = notes;
                db.updateNotes(subjectModule, subjectName);
                Toast.makeText(getApplicationContext(), "Successfully updated Notes", Toast.LENGTH_SHORT).show();
                notesDialog.dismiss();
                notesShow.setText(notes);
                subjectModule.setSubjectNotes(notes);
                notesAdd.setVisibility(View.GONE);
                layoutNotes.setVisibility(View.VISIBLE);
            }
        });
        cancelNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog.dismiss();
            }
        });
        notesDialog.show();
    }

    //adding routine
    private void addRoutine(String subjectRoutine) {
        //starting activity AdDRoutineActivity to add routine and sending the name of the subject
        Intent intent = new Intent(SubjectDetailActivity.this, AddRoutineActivity.class);
        intent.putExtra("SubjectName_key", subjectName);
        intent.putExtra("Routine_key", subjectRoutine);
        startActivity(intent);
        finish();
    }

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subject_upload:
                UploadSubject();
                break;
            case R.id.subject_reminder:
                addReminder();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //adding reminder to show notification
    private void addReminder() {
        databaseReminder = new DatabaseReminder(folder, getApplicationContext());
        if (databaseReminder.getReminderDate(subjectName).length() == 0) {
            databaseReminder.addToReminder(subjectName, "", "");
        }
        dialogRemainder = new Dialog(this);
        dialogRemainder.setContentView(R.layout.remainder_layout);
        layoutDate = (LinearLayout) dialogRemainder.findViewById(R.id.layout_date);
        layoutTime = (LinearLayout) dialogRemainder.findViewById(R.id.layout_time);
        date = (TextView) dialogRemainder.findViewById(R.id.remainder_date);
        time = (TextView) dialogRemainder.findViewById(R.id.remainder_time);
        Button exit = (Button) dialogRemainder.findViewById(R.id.cancel_dialog);
        Button setRemainder = (Button) dialogRemainder.findViewById(R.id.set_remainder);
        Button cancelRemainder = (Button) dialogRemainder.findViewById(R.id.cancel_remainder);
        cancelRemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OnAlarmReceiver.class);
                PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);
                alarmManager.cancel(pi);
                Toast.makeText(getApplicationContext(), "Cancelling Alarm", Toast.LENGTH_SHORT).show();
            }
        });

        ///date picker
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        layoutDate.setOnClickListener(this);
        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormatter.format(newDate.getTime()));
                requiredDate = dateFormatter.format(newDate.getTime());
                databaseReminder.UpdateReminderDate(requiredDate, subjectName);
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //timepicker
        layoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SubjectDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker0, int selectedHour, int selectedMinute) {
                        String formattedMinute;
                        if (selectedMinute >= 0 && selectedMinute <= 9) {
                            formattedMinute = "0" + selectedMinute;
                        } else {
                            formattedMinute = String.valueOf(selectedMinute);
                        }
                        timeHere = selectedHour + ":" + formattedMinute;
                        time.setText(timeHere);
                        databaseReminder.UpdateReminderTime(timeHere, subjectName);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        dateToReturn = databaseReminder.getReminderDate(subjectName);
        if (!dateToReturn.equals("")) {
            date.setText(dateToReturn);
        }
        timeToReturn = databaseReminder.getReminderTime(subjectName);
        if (!timeToReturn.equals("")) {
            time.setText(timeToReturn);
        }
        // on click  "Set Remainder & Exit"
        setRemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateToReturn.equals("") && !timeToReturn.equals(""))
                    setNotification(subjectName, subjectDeadline, subjectUpdater);
                dialogRemainder.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRemainder.dismiss();
            }
        });
        dialogRemainder.show();
    }

    // to show notification on the set time
    private void setNotification(String subject, String deadline, String user) {
        String getTime = databaseReminder.getReminderTime(subjectName);
        String getDate = databaseReminder.getReminderDate(subjectName);

        //using AlarmManager
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), OnAlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("subject_key", subject);
        bundle.putString("deadline_key", deadline);
        bundle.putString("user_key", user);
        i.putExtras(bundle);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);

        String value = (Integer.parseInt(getDate.substring(6, 10)) + "*" + Integer.parseInt(getDate.substring(3, 5)) + "*" +
                Integer.parseInt(getDate.substring(0, 2)) + "*" + Integer.parseInt(getTime.substring(0, 2)) + "*" +
                Integer.parseInt(getTime.substring(3, 5)));
        Log.i(TAG, "reminder ko :" + value);

        Calendar myCal = Calendar.getInstance();
        myCal.setTimeInMillis(System.currentTimeMillis());
        myCal.clear();
        // format set(year, month, day, hour, minute)
        myCal.set(Integer.parseInt(getDate.substring(6, 10)), (Integer.parseInt(getDate.substring(3, 5)) - 1),
                Integer.parseInt(getDate.substring(0, 2)), Integer.parseInt(getTime.substring(0, 2)),
                Integer.parseInt(getTime.substring(3, 5)));

        // on devices above and equal to kitkat version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pi);
            Toast.makeText(getApplicationContext(), "Alarm set for " + myCal.getTime().toLocaleString(), Toast.LENGTH_LONG).show();
        } else {
            // on devices below kitkat
            alarmManager.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pi);
            Toast.makeText(getApplicationContext(), "Alarm set for " + myCal.getTime().toLocaleString(), Toast.LENGTH_LONG).show();
        }
    }

    private void UploadSubject() {
        if (identifier.equals("local")) {
            Toast.makeText(getApplicationContext(), "Access Granted", Toast.LENGTH_SHORT).show();
            UploadLocalSubject();
        } else {
            //only perform if the username matches
            Toast.makeText(getApplicationContext(), "Access Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // when uploaded from "Local"
    // uploading to the server database
    private void UploadLocalSubject() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyConfig.ADDCMT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //If we are getting success from server
                String getResponse = response.toLowerCase();

                if (getResponse.contains("1")) {
                    Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error while uploading" + response, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String updatedNotes = db.getUpdatesNotes(subjectName);
                String updatedDeadline = db.getUpdatedDeadline(subjectName);
                //Adding parameters to request
                params.put(VolleyConfig.KEY_AUTHOR, subjectModule.getSubjectUpdater());
                params.put(VolleyConfig.KEY_SUBJECT, subjectModule.getSubjectName());
                params.put(VolleyConfig.KEY_ROUTINE, subjectModule.getSubjectRoutine());
                params.put(VolleyConfig.KEY_DEADLINE, updatedDeadline);
                params.put(VolleyConfig.KEY_NOTES, updatedNotes);
                params.put(VolleyConfig.KEY_UPLOAD, "");
                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
