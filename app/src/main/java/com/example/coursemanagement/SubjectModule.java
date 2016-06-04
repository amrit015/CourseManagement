package com.example.coursemanagement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object class
 * Extends Parcelable, bulk is passed through Intent when required
 */
public class SubjectModule implements Parcelable {
    public static final Parcelable.Creator<SubjectModule> CREATOR = new Parcelable.Creator<SubjectModule>() {
        //Generates instance of Parcelable class from a parcel

        @Override
        public SubjectModule createFromParcel(Parcel source) {
            /* Creates instance of new Parcelable class,instantiating it from the given Parcel
               whose data were previously written with writeToParcel
            */
            return new SubjectModule(source);
        }

        @Override
        public SubjectModule[] newArray(int size) {
            return new SubjectModule[size];
        }
    };

    String subjectName;
    String subjectRoutine;
    String subjectNotes; //assignment
    String subjectDeadline; //deadline
    String subjectUpdater;
    String showLayout;
    String identifier;
    int postId;

    public SubjectModule() {
        //default constructor
    }

    private SubjectModule(Parcel in) {
        /* reading back each field from the parcel in the same order it was written
           to the parcel
        */
        this.subjectName = in.readString();
        this.subjectRoutine = in.readString();
        this.subjectNotes = in.readString();
        this.subjectDeadline = in.readString();
        this.subjectUpdater = in.readString();
        this.showLayout = in.readString();
        this.identifier = in.readString();
        this.postId = in.readInt();
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectNotes() {
        return subjectNotes;
    }

    public void setSubjectNotes(String subjectNotes) {
        this.subjectNotes = subjectNotes;
    }

    public String getSubjectDeadline() {
        return subjectDeadline;
    }

    public void setSubjectDeadline(String subjectDeadline) {
        this.subjectDeadline = subjectDeadline;
    }

    public String getSubjectRoutine() {
        return subjectRoutine;
    }

    public void setSubjectRoutine(String subjectRoutine) {
        this.subjectRoutine = subjectRoutine;
    }

    public String getSubjectUpdater() {
        return subjectUpdater;
    }

    public void setSubjectUpdater(String subjectUpdater) {
        this.subjectUpdater = subjectUpdater;
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

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
 /* writing each field to the parcel,same order is used to read from parcel
        */
        dest.writeString(subjectName);
        dest.writeString(subjectRoutine);
        dest.writeString(subjectNotes);
        dest.writeString(subjectDeadline);
        dest.writeString(subjectUpdater);
        dest.writeString(showLayout);
        dest.writeString(identifier);
        dest.writeInt(postId);
    }
}
