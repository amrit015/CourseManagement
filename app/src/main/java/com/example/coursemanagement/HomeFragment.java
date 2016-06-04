package com.example.coursemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the default fragment called from MainActivity.
 */
public class HomeFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "HomeFragment";
    ArrayList<SubjectModule> list = new ArrayList<>();
    LinearLayout progressLayout;
    Context context;
    File folder;
    String subject, user;
    int position, postId;
    String currentUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        progressLayout = (LinearLayout) rootView.findViewById(R.id.headerProgress);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_home);
        mLayoutManager = new LinearLayoutManager(context);
        //creating folders
        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            folder = new File(Environment.getDataDirectory()
                    + "/CourseApplication/");
            folder.mkdirs();
            Log.i(TAG, "folder : " + folder);
        }
        // if phone DOES have sd card
        if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            folder = new File(Environment.getExternalStorageDirectory()
                    + "/CourseApplication/");
            folder.mkdirs();
            Log.i(TAG, "folder : " + folder);
        }
        //use volley to inflate here and obtain data from the internet
        getSubjects();
        //Fetching username from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(VolleyConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString(VolleyConfig.EMAIL_SHARED_PREF, "Not Available");
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //getting data from the server
    private void getSubjects() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, VolleyConfig.VIEWCMT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                list.clear();
                //If we are getting success from server
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("posts");
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String username = c.getString("username");
                        String subject = c.getString("subject");
                        String routine = c.getString("routine");
                        String notes = c.getString("notes");
                        String remainder = c.getString("remainder");
                        String upload = c.getString("upload");
                        int postId = c.getInt("id");

                        // if "1", we dont show the item in the homefragment
                        if (c.getInt("delete") == 0) {
                            SubjectModule subjectModule = new SubjectModule();
                            subjectModule.setSubjectName(subject);
                            subjectModule.setSubjectRoutine(routine);
                            subjectModule.setSubjectNotes(notes);
                            subjectModule.setSubjectDeadline(remainder);
                            subjectModule.setSubjectUpdater(username);
                            subjectModule.setShowLayout("false");
                            subjectModule.setIdentifier("home");
                            subjectModule.setPostId(postId);
                            list.add(subjectModule);
                            Log.i(TAG, "updater :" + username);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //displaying the obtained data
                displaySubjects();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                    }
                }) {
        };
        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void displaySubjects() {
        progressLayout.setVisibility(View.GONE);
        mAdapter = new MyRecyclerViewAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    //adding context menu on longclick to each cards
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Cancel");
    }

    //defining context menu actions
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //getting subject, position and postId of the Longclicked item from the recyclerview adadpter
        try {
            subject = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getSubject();
            position = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getPosition();
            postId = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getPostId();
            user = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getUpdater();
            Log.i(TAG, "title: " + subject);
            Log.i(TAG, "position: " + position);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            return super.onContextItemSelected(item);
        }
        if (item.getTitle() == "Delete") {
            if (currentUser.equals(user)) {
                removeItem();
            } else {
                Toast.makeText(getActivity(), "Access Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            item.collapseActionView();
        }
        return super.onContextItemSelected(item);
    }

    // hiding the item when the user presses "Delete"
    private void removeItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VolleyConfig.DELETE_CMT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //If we are getting success from server
                String getResponse = response.toLowerCase();
                Log.i(TAG, "Response while sending delete command : " + response);

                if (getResponse.contains("1")) {
                    Toast.makeText(getActivity(), "Successfully deleted notes", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error while deleting notes:" + response, Toast.LENGTH_SHORT).show();
                }
                //updating the list through server and populating with the updated data
                progressLayout.setVisibility(View.VISIBLE);
                getSubjects();
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
                params.put(VolleyConfig.KEY_POSTID, String.valueOf(postId));
                params.put(VolleyConfig.KEY_DELETE, "1");
                Log.i(TAG, "title to delete :" + subject);
                //returning parameter
                return params;
            }
        };
        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
