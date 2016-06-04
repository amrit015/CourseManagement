package com.example.coursemanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the opening activity. The login screen is displayed here.
 */
public class StartUpActivity extends AppCompatActivity {
    EditText getUsername, getPassword;
    Button logIn;
    Button register;
    String insertedUsername;
    String insertedPassword;
    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        //neeeded to restore the app after pressing home button
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        getUsername = (EditText) findViewById(R.id.username);
        getPassword = (EditText) findViewById(R.id.password);
        logIn = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        logIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                login();
            }

        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(StartUpActivity.this, RegisterUserActivity.class);
                startActivity(main);
            }
        });
    }

    private void login() {
        //Getting values from edit text
        insertedUsername = getUsername.getText().toString();
        insertedPassword = getPassword.getText().toString();

        if (insertedUsername.equals("") || insertedPassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Empty username or password", Toast.LENGTH_SHORT).show();
        } else {
            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyConfig.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            String getResponse = response.toLowerCase();
//                            Toast.makeText(getApplicationContext(), "hell:" + response, Toast.LENGTH_SHORT).show();

                            if (getResponse.contains("1")) {
                                //Creating a shared preference
                                SharedPreferences sharedPreferences = StartUpActivity.this.getSharedPreferences(VolleyConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                //Adding values to editor
                                editor.putBoolean(VolleyConfig.LOGGEDIN_SHARED_PREF, true);
                                editor.putString(VolleyConfig.EMAIL_SHARED_PREF, insertedUsername);

                                //Saving values to editor
                                editor.commit();

                                //Starting profile activity
                                Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //If the server response is not success
                                //Displaying an error message on toast
                                Toast.makeText(StartUpActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
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
                    //Adding parameters to request
                    params.put(VolleyConfig.KEY_EMAIL, insertedUsername);
                    params.put(VolleyConfig.KEY_PASSWORD, insertedPassword);

                    //returning parameter
                    return params;
                }
            };
            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(VolleyConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(VolleyConfig.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if (loggedIn) {
            //We will start the Profile Activity
            Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
