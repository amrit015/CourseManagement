package com.example.coursemanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * It creates database to save the subjects and details of the subject (routine, deadline and notes) and
 * saves the database on the local storage of the phone.
 */
public class DatabaseHelperSubjects extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static String SUBJECT_NAME = "subjectDatabase", SUBJECT_TABLE = "subjectTable";
    SQLiteDatabase db;
    Context c;
    private ArrayList<SubjectModule> listSubject = new ArrayList<SubjectModule>();
    private ArrayList<SubjectModule> updatedList = new ArrayList<SubjectModule>();

    public DatabaseHelperSubjects(File filename, Context context) {
        super(context, filename + ("/") + SUBJECT_NAME, null, DATABASE_VERSION);
        c = context;
    }

    //creating database if it doesn't exist
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS subjectTable("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "subject TEXT,"
                + "notes VARCHAR,"
                + "updater VARCHAR,"
                + "routine VARCHAR,"
                + "addPlus TEXT,"
                + "identifier TEXT,"
                + "deadline VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + SUBJECT_TABLE);
        onCreate(db);
    }

    //adding subjects, routine, and so on to the database
    public void addSubjects(SubjectModule sm) {
        // TODO Auto-generated method stub
        db = this.getReadableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("subject", sm.subjectName);
        contentvalues.put("notes", sm.subjectNotes);
        contentvalues.put("deadline", sm.subjectDeadline);
        contentvalues.put("routine", sm.subjectRoutine);
        contentvalues.put("updater", sm.subjectUpdater);
        contentvalues.put("addPlus", sm.showLayout);
        contentvalues.put("identifier", sm.identifier);
        db.insert(SUBJECT_TABLE, null, contentvalues);
        db.close();
    }

    //getting the fields from the database
    public ArrayList<SubjectModule> getSubjects() {
        // TODO Auto-generated method stub
        listSubject.clear();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from subjectTable", null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    SubjectModule item = new SubjectModule();
                    item.subjectName = cursor.getString(cursor
                            .getColumnIndex("subject"));
                    item.subjectNotes = cursor.getString(cursor
                            .getColumnIndex("notes"));
                    item.subjectDeadline = cursor.getString(cursor
                            .getColumnIndex("deadline"));
                    item.subjectRoutine = cursor.getString(cursor
                            .getColumnIndex("routine"));
                    item.subjectUpdater = cursor.getString(cursor
                            .getColumnIndex("updater"));
                    item.showLayout = cursor.getString(cursor
                            .getColumnIndex("addPlus"));
                    item.identifier = cursor.getString(cursor
                            .getColumnIndex("identifier"));
                    listSubject.add(item);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return listSubject;
    }

    //getting only the updates notes
    public String getUpdatesNotes(String subject_id) {
        String notes = "";
        db = this.getWritableDatabase();
        // getting time when unique_id gets matched
        Cursor cursor = db.query(SUBJECT_TABLE, new String[]{"notes"}, "subject= ?", new String[]{subject_id}, null, null, null, null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    notes = cursor.getString(cursor
                            .getColumnIndex("notes"));
                } while (cursor.moveToNext());
            }
        }
        db.close();
        cursor.close();
        return notes;
    }

    //getting only the updated deadline
    public String getUpdatedDeadline(String subject_id) {
        String deadline = "";
        db = this.getWritableDatabase();
        // getting time when unique_id gets matched
        Cursor cursor = db.query(SUBJECT_TABLE, new String[]{"deadline"}, "subject= ?", new String[]{subject_id}, null, null, null, null);
        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    deadline = cursor.getString(cursor
                            .getColumnIndex("deadline"));
                } while (cursor.moveToNext());
            }
        }
        db.close();
        cursor.close();
        return deadline;
    }

    //updating the fields
    public void updateSubject(SubjectModule sm, String subject) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        cv1.put("subject", sm.subjectName);
        cv1.put("notes", sm.subjectNotes);
        cv1.put("deadline", sm.subjectDeadline);
        cv1.put("routine", sm.subjectRoutine);
        cv1.put("updater", sm.subjectUpdater);
        cv1.put("addPlus", sm.showLayout);
        cv1.put("identifier", sm.identifier);
        db.update(SUBJECT_TABLE, cv1, "subject = ?", new String[]{subject});
        db.close();
    }

    //updating only the routine
    public void updateRoutine(SubjectModule sm, String subject) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        cv1.put("routine", sm.subjectRoutine);
        db.update(SUBJECT_TABLE, cv1, "subject = ?", new String[]{subject});
        db.close();
    }

    //updating only note
    public void updateNotes(SubjectModule sm, String subject) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        cv1.put("notes", sm.subjectNotes);
        db.update(SUBJECT_TABLE, cv1, "subject = ?", new String[]{subject});
        db.close();
    }

    //updating only deadline
    public void updateDeadline(SubjectModule sm, String subject) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        cv1.put("deadline", sm.subjectDeadline);
        db.update(SUBJECT_TABLE, cv1, "subject = ?", new String[]{subject});
        db.close();
    }

    //removing the subject row from the database if necessary
    public void removeSubject(String subject) {
        // TODO Auto-generated method stub
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("subjectTable", "subject = ?", new String[]{subject});
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}