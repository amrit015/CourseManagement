package com.example.coursemanagement;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * The subject are saved on the local database here, values are recieved through edittext and displayed using
 * recyclerview adapter. Subject are also edited and deleted from here.
 */
public class LocalFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "LocalFragment";
    EditText newSubject;
    Button subjectSave, subjectCancel;
    Context context;
    File folder;
    DatabaseHelperSubjects db;
    SubjectModule subjectModule;
    String currentUser;
    EditText dialogSubjectTitle;
    Dialog dialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<SubjectModule> subjectArray = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_local, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(context);
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
        //Fetching username from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(VolleyConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString(VolleyConfig.EMAIL_SHARED_PREF, "Not Available");

        db = new DatabaseHelperSubjects(folder, getActivity());
        //setting up floating button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_new_subjects);
                dialog.show();
                newSubject = (EditText) dialog.findViewById(R.id.edittext_subject);
                subjectCancel = (Button) dialog.findViewById(R.id.subject_cancel);
                subjectSave = (Button) dialog.findViewById(R.id.subject_add);
                subjectSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveSubject();
                        dialog.dismiss();
                        mAdapter = new MyRecyclerViewAdapter(getDataSet());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
                subjectCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return rootView;
    }

    // saving the subjects on the database
    private void saveSubject() {
        String subjectToAdd = newSubject.getText().toString();
        subjectModule = new SubjectModule();
        subjectModule.subjectName = subjectToAdd;
        subjectModule.subjectNotes = "";
        subjectModule.subjectUpdater = currentUser;
        subjectModule.subjectRoutine = "";
        subjectModule.subjectDeadline = "";
        subjectModule.showLayout = "true";
        subjectModule.identifier = "local";
        db.addSubjects(subjectModule);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //getting the subject from the database and passing to the recyclerview adapter
    private ArrayList<SubjectModule> getDataSet() {
        subjectArray.clear();
        ArrayList<SubjectModule> list = db.getSubjects();
        for (int i = list.size() - 1; i >= 0; i--) {
            String nTitle = list.get(i).getSubjectName();
            String nUpdater = list.get(i).getSubjectUpdater();
            String nRoutine = list.get(i).getSubjectRoutine();
            String nNotes = list.get(i).getSubjectNotes();
            String nRemainder = list.get(i).getSubjectDeadline();
            String nAdd = list.get(i).getShowLayout();
            String nIdentifier = list.get(i).getIdentifier();
            subjectModule = new SubjectModule();
            subjectModule.setSubjectName(nTitle);
            subjectModule.setSubjectUpdater(nUpdater);
            subjectModule.setSubjectDeadline(nRemainder);
            subjectModule.setSubjectNotes(nNotes);
            subjectModule.setSubjectRoutine(nRoutine);
            subjectModule.setShowLayout(nAdd);
            subjectModule.setIdentifier(nIdentifier);
            subjectArray.add(subjectModule);
        }
        return subjectArray;
    }

    //adding context menu on longclick to each cards
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Cancel");
    }

    //defining context menu actions
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String subject, note, routine, deadline, layout, identify, updater;
        int position;
        try {
            subject = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getSubject();
            position = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getPosition();
            routine = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getRoutine();
            deadline = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getDeadline();
            note = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getNotes();
            identify = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getIdentifier();
            layout = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getShowLayout();
            updater = ((MyRecyclerViewAdapter) mRecyclerView.getAdapter()).getUpdater();
            Log.i(TAG, "title: " + subject);
            Log.i(TAG, "position: " + position);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            return super.onContextItemSelected(item);
        }
        if (item.getTitle() == "Edit") {
            editItem(subject, position, routine, deadline, note, identify, layout, updater);
        } else if (item.getTitle() == "Delete") {
            removeItem(subject, position);
        } else {
            item.collapseActionView();
        }
        return super.onContextItemSelected(item);
    }

    //removing the subject from the database and recyclerview
    private void removeItem(String subject, int position) {
        subjectArray.remove(position);
        db.removeSubject(subject);
        mAdapter.notifyDataSetChanged();
    }

    //editing the name of the subject on the database
    private void editItem(final String subject, int position, final String routine, final String deadline, final String note, final String identify, final String layout, final String updater) {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_edit_subject);
        dialog.show();
        dialogSubjectTitle = (EditText) dialog.findViewById(R.id.edit_subject_title);
        Button dialog_save = (Button) dialog.findViewById(R.id.subject_edit_done);
        Button dialog_cancel = (Button) dialog.findViewById(R.id.subject_edit_cancel);
        dialogSubjectTitle.setText(subject);

        dialog_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectModule = new SubjectModule();
                subjectModule.subjectName = dialogSubjectTitle.getText().toString();
                subjectModule.subjectNotes = note;
                subjectModule.subjectRoutine = routine;
                subjectModule.subjectUpdater = updater;
                subjectModule.subjectDeadline = deadline;
                subjectModule.showLayout = layout;
                subjectModule.identifier = identify;
                db.updateSubject(subjectModule, subject);
                mAdapter.notifyDataSetChanged();
                mAdapter = new MyRecyclerViewAdapter(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
                Toast.makeText(getActivity(), "Successfully Edited", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //updating the contents of recyclerview on onResume
    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}