package com.example.employeeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "phone TEXT UNIQUE NOT NULL," +
                "address TEXT NOT NULL," +
                "job_title TEXT NOT NULL," +
                "start_date TEXT NOT NULL," + // YYYY-MM-DD
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL CHECK (role IN ('Admin', 'Employee'))" +
                ");";

        String createPtoRequestTable = "CREATE TABLE PtoRequest (" +
                "id INTEGER PRIMARY KEY," +
                "requester_id INTEGER NOT NULL," +
                "approver_id INTEGER NOT NULL," +
                "start_date TEXT NOT NULL," + // YYYY-MM-DD HH:MM:SS
                "end_date TEXT NOT NULL," +   // YYYY-MM-DD HH:MM:SS
                "status TEXT NOT NULL CHECK (status IN ('Approved', 'Waiting', 'Denied'))," +
                "request_comment TEXT," +
                "FOREIGN KEY (requester_id) REFERENCES User (id)," +
                "FOREIGN KEY (approver_id) REFERENCES User (id)" +
                ");";

        String createLineTable = "CREATE TABLE Line (" +
                "manager_id INT NOT NULL," +
                "subordinate_id INT NOT NULL," +
                "FOREIGN KEY (manager_id) REFERENCES User (id)," +
                "FOREIGN KEY (subordinate_id) REFERENCES User (id)" +
                ");";

        db.execSQL(createUserTable);
        db.execSQL(createPtoRequestTable);
        db.execSQL(createLineTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User;");
        db.execSQL("DROP TABLE IF EXISTS PtoRequest;");
        db.execSQL("DROP TABLE IF EXISTS Line;");

        onCreate(db);
    }
}
