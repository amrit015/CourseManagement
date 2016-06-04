package com.example.coursemanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This is the adapter for RecyclerView. The adapter must extend its child
 * i.e. RecyclerView.Adapter<MyRecyclerViewAdapter.ItemObjectHolder> and must have a ViewHolder
 * which extends RecyclerView.ViewHolder
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.SubjectHolder> {
    private static final String TAG = "MyRecyclerViewAdapter";
    Context context;
    SubjectModule subjectModule;
    private ArrayList<SubjectModule> mDataset;
    private String subject;
    private String notes;
    private String routine;
    private String deadline;
    private String showLayout;
    private String identifier;
    private String updater;
    private int position;
    private int postId;

    public MyRecyclerViewAdapter(ArrayList mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_view_myrecycler, parent, false);
        context = parent.getContext();
        SubjectHolder subjectHolder = new SubjectHolder(view);
        return subjectHolder;
    }

    @Override
    public void onBindViewHolder(final SubjectHolder holder, final int position) {
        //binding the data to the view ie. recyclerview and cardview
        subjectModule = mDataset.get(position);
        holder.subjectTitle.setText(subjectModule.getSubjectName());
        holder.subjectUpdater.setText("Updated by " + subjectModule.getSubjectUpdater());
        holder.subjectTitle.setVisibility(View.VISIBLE);
        holder.subjectUpdater.setVisibility(View.VISIBLE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectModule module = mDataset.get(position); //getting position for each item
                Intent intent = new Intent(context, SubjectDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Subject_key", module);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        //saving title,notes and position of each cards for contextmenu
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                subjectModule = mDataset.get(position);
                setSubject(holder.subjectTitle.getText().toString());
                setNotes(subjectModule.getSubjectNotes());
                setDeadline(subjectModule.getSubjectDeadline());
                setRoutine(subjectModule.getSubjectRoutine());
                setIdentifier(subjectModule.getIdentifier());
                setShowLayout(subjectModule.getShowLayout());
                setUpdater(subjectModule.getSubjectUpdater());
                setPostId(subjectModule.getPostId());
                setPosition(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int getPosition() {
        return position;
    }

    // saving and accessing subject,notes,position

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getRoutine() {
        return routine;
    }

    public void setRoutine(String routine) {
        this.routine = routine;
    }

    public String getShowLayout() {
        return showLayout;
    }

    public void setShowLayout(String showLayout) {
        this.showLayout = showLayout;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public class SubjectHolder extends RecyclerView.ViewHolder {
        TextView subjectTitle;
        TextView subjectUpdater;
        CardView cardView;

        public SubjectHolder(View itemView) {
            super(itemView);
            subjectTitle = (TextView) itemView.findViewById(R.id.subject_title);
            subjectUpdater = (TextView) itemView.findViewById(R.id.subject_update);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
