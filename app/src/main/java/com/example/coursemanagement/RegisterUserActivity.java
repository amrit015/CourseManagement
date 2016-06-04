package com.example.coursemanagement;

import android.content.Intent;
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
 * Registering the username and password on the server database
 */
public class RegisterUserActivity extends AppCompatActivity {
    Button register;
    EditText getName, getPassword, getInstitution, getEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        register = (Button) findViewById(R.id.register_user);
        getName = (EditText) findViewById(R.id.register_username);
        getPassword = (EditText) findViewById(R.id.register_password);
        getInstitution = (EditText) findViewById(R.id.register_institution);
        getEmailId = (EditText) findViewById(R.id.register_email);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        final String username = getName.getText().toString().trim();
        final String passowrd = getPassword.getText().toString().trim();

        if (username.equals("") || passowrd.equals("")) {
            Toast.makeText(getApplicationContext(), "Empty username or password", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyConfig.REGISTER_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //If we are getting success from server
                    String getResponse = response.toLowerCase();
                    Toast.makeText(getApplicationContext(), "hell:" + response, Toast.LENGTH_SHORT).show();

                    if (getResponse.contains("1")) {
                        Intent i = new Intent(getApplicationContext(), StartUpActivity.class);
                        Toast.makeText(getApplicationContext(), "Success!!", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "hell error:" + response, Toast.LENGTH_SHORT).show();
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
                    params.put(VolleyConfig.KEY_EMAIL, username);
                    params.put(VolleyConfig.KEY_PASSWORD, passowrd);

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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterUserActivity.this, StartUpActivity.class));
        finish();
    }
}
