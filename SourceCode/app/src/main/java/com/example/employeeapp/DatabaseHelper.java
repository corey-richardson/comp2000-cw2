package com.example.employeeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
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
import android.widget.Toast;

import com.google.gson.Gson;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 7;

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
                "holiday_allowance INT NOT NULL CHECK (holiday_allowance > 0) DEFAULT 0," +
                "role TEXT NOT NULL CHECK (role IN ('Admin', 'Employee'))" +
                ");";

        String createUserSettingsTable = "CREATE TABLE UserSettings (" +
                "user_id INTEGER PRIMARY KEY, " +
                "pto_notifications INTEGER NOT NULL, " +
                "details_notifications INTEGER NOT NULL, " +
                "dark_theme INTEGER NOT NULL, " +
                "red_green_theme INTEGER NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES User (id) " +
                ");";

        String createPtoRequestTable = "CREATE TABLE PtoRequest (" +
                "id INTEGER PRIMARY KEY," +
                "requester_id INTEGER NOT NULL," +
                "start_date TEXT NOT NULL," + // YYYY-MM-DD HH:MM:SS
                "end_date TEXT NOT NULL," +   // YYYY-MM-DD HH:MM:SS
                "status TEXT NOT NULL CHECK (status IN ('Approved', 'Waiting', 'Denied'))," +
                "request_comment TEXT," +
                "FOREIGN KEY (requester_id) REFERENCES User (id)," +
                "UNIQUE(requester_id, start_date, end_date)" +
                ");";

        String createLineTable = "CREATE TABLE Line (" +
                "manager_id INT NOT NULL," +
                "subordinate_id INT NOT NULL," +
                "FOREIGN KEY (manager_id) REFERENCES User (id)," +
                "FOREIGN KEY (subordinate_id) REFERENCES User (id)" +
                ");";

        try {
            db.execSQL(createUserTable);
            db.execSQL(createUserSettingsTable);
            db.execSQL(createPtoRequestTable);
            db.execSQL(createLineTable);
            Log.d("DatabaseCreationSuccess", (DATABASE_NAME + " " + DATABASE_VERSION + " was created."));
        } catch (Exception e) {
            Log.d("DatabaseCreationError", Log.getStackTraceString(e));
        }

        db.execSQL("CREATE INDEX idx_user_first_name ON User (first_name);");
        db.execSQL("CREATE INDEX idx_user_last_name ON User (last_name);");
        db.execSQL("CREATE INDEX idx_user_email ON User (email);");
        db.execSQL("CREATE INDEX idx_user_id_settings ON UserSettings (user_id);");
        db.execSQL("CREATE INDEX idx_pto_request_requester ON PtoRequest (requester_id);");

        db.execSQL("INSERT INTO User (first_name, last_name, email, phone, address, job_title, " +
                "start_date, password, role, holiday_allowance) " +
                "VALUES ('Admin', 'User', 'admin@example.com', '1234567890', '123 Admin Street', " +
                "'System Admin', '2024-01-01', 'admin_password', 'Admin', 30);");
        db.execSQL("INSERT INTO User (first_name, last_name, email, phone, address, job_title, " +
                "start_date, password, role, holiday_allowance) " +
                "VALUES ('John', 'Doe', 'john.doe@example.com', '0987654321', '456 Employee Lane', " +
                "'Software Engineer', '2024-01-01', 'employee_password', 'Employee', 20);");
        db.execSQL("INSERT INTO UserSettings (user_id, pto_notifications, details_notifications, " +
                "dark_theme, red_green_theme)" +
                "VALUES (1, 1, 1, 0, 0);");
        db.execSQL("INSERT INTO UserSettings (user_id, pto_notifications, details_notifications, " +
                "dark_theme, red_green_theme)" +
                "VALUES (2, 1, 1, 1, 0);");

        Log.d("onCreate", "onCreate was run.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User;");
        db.execSQL("DROP TABLE IF EXISTS UserSettings;");
        db.execSQL("DROP TABLE IF EXISTS PtoRequest;");
        db.execSQL("DROP TABLE IF EXISTS Line;");

        onCreate(db);
    }

    // Helper Queries
    public Employee cursorToEmployee(Cursor cursor) {
        return new Employee(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                cursor.getString(cursor.getColumnIndexOrThrow("job_title")),
                cursor.getString(cursor.getColumnIndexOrThrow("start_date")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getInt(cursor.getColumnIndexOrThrow("holiday_allowance")),
                cursor.getString(cursor.getColumnIndexOrThrow("role"))
        );
    }

    public PtoRequest cursorToPtoRequest(Cursor cursor) {
        return new PtoRequest(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("requester_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getString(cursor.getColumnIndexOrThrow("start_date")),
                cursor.getString(cursor.getColumnIndexOrThrow("end_date")),
                cursor.getString(cursor.getColumnIndexOrThrow("request_comment"))
        );
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

        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM PtoRequest", null);
            if (cursor.moveToFirst()) {
                do {
                    ptoRequestList.add(cursorToPtoRequest(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error fetching PTO requests: " + e.getMessage());
        } finally {
            if (cursor != null) { cursor.close(); }
            if (db != null) { db.close(); }
        }

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

    public void clearCurrentUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("currentUser");
        editor.apply();
    }

    // Writes user to SharedPreferences if exists in database
    public boolean authenticateUser(Context context, String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE email = ? AND password = ?",
                new String[]{ email, password });

        if (cursor != null && cursor.moveToFirst()) {
            Employee employee = cursorToEmployee(cursor);
            saveCurrentUser(context, employee);

            Log.d("Login", "Login Succeeded, " + employee.getFull_name() + ".");
            cursor.close();
            db.close();
            return true;
        } else {
            Log.d("Login", "Login Failed, User doesn't exist.");
            cursor.close();
            db.close();
            return false;
        }
    }

    public void updateUserInDatabase(Context context, Employee employee) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // For UPDATEs, use Prepared Statements rather than rawQuery
        String updateQuery = "UPDATE User SET " +
                "first_name = ?, " +
                "last_name = ?, " +
                "email = ?, " +
                "phone = ?, " +
                "address = ?, " +
                "job_title = ?, " +
                "start_date = ?, " +
                "password = ?, " +
                "role = ?, " +
                "holiday_allowance = ? " +
                "WHERE id = ?";

        db.execSQL(updateQuery, new Object[]{
                employee.getFirst_name(),
                employee.getLast_name(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getAddress(),
                employee.getJob_title(),
                employee.getStart_date(),
                employee.getPassword(),
                employee.getRole(),
                employee.getHoliday_allowance(),
                employee.getId()
        });

        db.close();
    }

    // https://www.geeksforgeeks.org/how-to-delete-data-in-sqlite-database-in-android/
    public void deletePtoRequest(int id) {

        try (SQLiteDatabase db = getWritableDatabase()) { // AndroidStudio suggested this \_O_/
            db.delete("PtoRequest", "id = ?", new String[]{Integer.toString(id)});
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error deleting PTO request " + id, e);
            throw e; // Propagates the error to PtoAdapter::cancelPtoRequest to Toast in context
        }
        // statement automatically manages resources and ensures they are closed after use
    }

    public void updateUserSettings(int userId, UserSettings userSettings) {
        ContentValues values = new ContentValues();

        try (SQLiteDatabase db = getWritableDatabase()) {
            values.put("pto_notifications", userSettings.getPtoNotifications() ? 1 : 0);
            values.put("details_notifications", userSettings.getDetailsNotifications() ? 1 : 0);
            values.put("dark_theme", userSettings.getDarkTheme() ? 1 : 0);
            values.put("red_green_theme", userSettings.getRedGreenTheme() ? 1 : 0);

            db.update("UserSettings", values, "user_id = ?",
                    new String[]{ Integer.toString(userId) });

            Log.d("UpdateSettings", "SettingsUpdated: " + userId);
        } catch (Exception e) {
            Log.e("UpdateUserSettings", "Failed to update settings.");
        }
    }

    public UserSettings loadUserSettings(Employee currentUser) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT pto_notifications, details_notifications, dark_theme, red_green_theme " +
                "FROM UserSettings " +
                "WHERE user_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(currentUser.getId())});
        UserSettings userSettings = null;

        if (cursor.moveToFirst()) {
            boolean ptoNotifications = cursor.getInt(cursor.getColumnIndex("pto_notifications")) == 1;
            boolean detailsNotifications = cursor.getInt(cursor.getColumnIndex("details_notifications")) == 1;
            boolean darkTheme = cursor.getInt(cursor.getColumnIndex("dark_theme")) == 1;
            boolean redGreenTheme = cursor.getInt(cursor.getColumnIndex("red_green_theme")) == 1;

            userSettings = new UserSettings(ptoNotifications, detailsNotifications, darkTheme, redGreenTheme);
        }

        cursor.close();
        db.close();

        return userSettings;
    }

    public void insertUser(Employee newEmployee) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues userValues = new ContentValues();
        ContentValues settingsValues = new ContentValues();

        db.beginTransaction();
        try {
            userValues.put("first_name", newEmployee.getFirst_name());
            userValues.put("last_name", newEmployee.getLast_name());
            userValues.put("email", newEmployee.getEmail());
            userValues.put("phone", newEmployee.getPhone());
            userValues.put("address", newEmployee.getAddress());
            userValues.put("job_title", newEmployee.getJob_title());
            userValues.put("start_date", newEmployee.getStart_date());
            userValues.put("password", newEmployee.getPassword());
            userValues.put("holiday_allowance", newEmployee.getHoliday_allowance());
            userValues.put("role", newEmployee.getRole());

            long userId = db.insert("User", null, userValues);
            if (userId == -1) { throw new SQLException("Failed to insert new user."); }

            settingsValues.put("user_id", userId);
            settingsValues.put("pto_notifications", 1);
            settingsValues.put("details_notifications", 1);
            settingsValues.put("dark_theme", 1);
            settingsValues.put("red_green_theme", 0);

            long settingsId = db.insert("UserSettings", null, settingsValues);
            if (settingsId == -1) { throw new SQLException("Failed to insert new user's settings."); }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("InsertUser", "Failed to insert new user. " + e);
            throw new SQLException("Failed to insert new user.");
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
