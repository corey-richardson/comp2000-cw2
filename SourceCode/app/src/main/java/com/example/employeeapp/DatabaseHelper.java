package com.example.employeeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 4;

    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton pattern to prevent multiple instances of the database helper
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
            Log.d("DatabaseInstance", "Instance created.");
        } else {
            Log.d("DatabaseInstance", "Instance carried forward.");
        }
        return instance;
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
                "holiday_allowance INT NOT NULL CHECK (holiday_allowance > 0)," +
                "role TEXT NOT NULL CHECK (role IN ('Admin', 'Employee'))" +
                ");";

        String createPtoRequestTable = "CREATE TABLE PtoRequest (" +
                "id INTEGER PRIMARY KEY," +
                "requester_id INTEGER NOT NULL," +
                "start_date TEXT NOT NULL," + // YYYY-MM-DD HH:MM:SS
                "end_date TEXT NOT NULL," +   // YYYY-MM-DD HH:MM:SS
                "status TEXT NOT NULL CHECK (status IN ('Approved', 'Waiting', 'Denied'))," +
                "request_comment TEXT," +
                "FOREIGN KEY (requester_id) REFERENCES User (id)" +
                ");";

        String createLineTable = "CREATE TABLE Line (" +
                "manager_id INT NOT NULL," +
                "subordinate_id INT NOT NULL," +
                "FOREIGN KEY (manager_id) REFERENCES User (id)," +
                "FOREIGN KEY (subordinate_id) REFERENCES User (id)" +
                ");";

        try {
            db.execSQL(createUserTable);
            db.execSQL(createPtoRequestTable);
            db.execSQL(createLineTable);
            Log.d("DatabaseCreationSuccess", (DATABASE_NAME + " " + DATABASE_VERSION + " was created."));
        } catch (Exception e) {
            Log.d("DatabaseCreationError", Log.getStackTraceString(e));
        }

        db.execSQL("CREATE INDEX idx_user_first_name ON User (first_name);");
        db.execSQL("CREATE INDEX idx_user_last_name ON User (last_name);");
        db.execSQL("CREATE INDEX idx_user_email ON User (email);");
        db.execSQL("CREATE INDEX idx_pto_request_requester ON PtoRequest (requester_id);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User;");
        db.execSQL("DROP TABLE IF EXISTS PtoRequest;");
        db.execSQL("DROP TABLE IF EXISTS Line;");

        onCreate(db);
    }

    // Helper Queries
    public Employee cursorToEmployee(Cursor cursor) {
        // Convert SQLite Date TEXT to Java.Date type
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateString = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));

        Date startDate;
        try {
            startDate = dateFormat.parse(startDateString);
        } catch (ParseException e) {
            Log.d("DateParseError", "Couldn't parse " + startDateString + " to a Java.Date object.");
            startDate = null;
        }

        Employee employee = new Employee(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_title")),
                startDate,
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getInt(cursor.getColumnIndexOrThrow("holiday_allowance")),
                cursor.getString(cursor.getColumnIndexOrThrow("role"))
        );

        cursor.close();
        return employee;
    }

    public PtoRequest cursorToPtoRequest(Cursor cursor) {
        // Convert SQLite Date TEXT to Java.Date type
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateString = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
        String endDateString = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));

        Date startDate, endDate;
        try {
            startDate = dateFormat.parse(startDateString);
        } catch (ParseException e) {
            Log.e("DateParseError", "Couldn't parse " + startDateString + "to a Java.Date object.");
            startDate = null;
        }
        try {
            endDate = dateFormat.parse(endDateString);
        } catch (ParseException e) {
            Log.e("DateParseError", "Couldn't parse " + endDateString + "to a Java.Date object.");
            endDate = null;
        }

        PtoRequest ptoRequest = new PtoRequest(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("requester_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                startDate,
                endDate,
                cursor.getString(cursor.getColumnIndexOrThrow("request_comment"))
        );

        cursor.close();
        return ptoRequest;
    }

    // Common Queries
    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM User", null);

        if (cursor.moveToFirst()) {
            do {
                employeeList.add(cursorToEmployee(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return employeeList;
    }

    public List<PtoRequest> getAllPtoRequests() {
        List<PtoRequest> ptoRequestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM PtoRequest", null);

        if (cursor.moveToFirst()) {
            do {
                ptoRequestList.add(cursorToPtoRequest(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ptoRequestList;
    }

    public List<PtoRequest> getAllPtoRequestsByRequester(int requesterId) {
        List<PtoRequest> ptoRequestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM PtoRequest WHERE id = ?",
                new String[]{Integer.toString(requesterId)});

        if (cursor.moveToFirst()) {
            do {
                ptoRequestList.add(cursorToPtoRequest(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ptoRequestList;
    }

    public Employee getManager(int subordinateId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT manager_id FROM Line WHERE subordinate_id = ?",
                new String[]{Integer.toString(subordinateId)} );

        // No manager found
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return null;
        }

        int managerId = cursor.getInt(cursor.getColumnIndexOrThrow("manager_id"));

        // Query to get managers details
        Cursor managerCursor = db.rawQuery(
                "SELECT * FROM User WHERE id = ?",
                new String[]{Integer.toString(managerId)}
        );

        // Unpack manager details and create Employee instance, *query
        if (managerCursor.moveToFirst()) {
            return cursorToEmployee(managerCursor);
        } else {
            cursor.close();
            // Manager not found in User table
            return null;
        }
    }

    public void saveCurrentUser(Context context, Employee employee) {
        Gson gson = new Gson();
        String employeeJson = gson.toJson(employee);

        SharedPreferences sharedPreferences = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentUser", employeeJson);
        editor.apply();
    }

    // Retrieve Employee from SharedPreferences
    public Employee loadCurrentUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String employeeJson = sharedPreferences.getString("currentUser", null);

        if (employeeJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(employeeJson, Employee.class);
        }

        return null; // No current user
    }

    // Writes user to SharedPreferences if exists in database
    public boolean authenticateUser(Context context, String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE email = ? AND password = ?",
                new String[]{ email, password });
        boolean exists = cursor.getCount() > 0;

        if (!exists) {
            Log.d("Login", "Login Failed, User doesn't exist.");
            return false;
        }

        Employee employee = cursorToEmployee(cursor);
        saveCurrentUser(context, employee);
        Log.d("Login", "Login Succeeded, " + employee.getFull_name() + ".");

        cursor.close();
        db.close();

        return true;
    }
}
