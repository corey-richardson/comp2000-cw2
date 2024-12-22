package com.example.employeeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import com.google.gson.Gson;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Class attributes
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 14;
    private static DatabaseHelper instance;


    // Constructor
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


    // Database Creation and Upgrade methods
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "department TEXT NOT NULL," +
                "salary REAL NOT NULL CHECK (salary > 0) DEFAULT 0," +
                "start_date TEXT NOT NULL," + // YYYY-MM-DD
                "holiday_allowance INT NOT NULL CHECK (holiday_allowance > 0) DEFAULT 0," +
                "password TEXT NOT NULL," +
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

        db.execSQL("INSERT INTO User (first_name, last_name, email, department, salary, start_date, " +
                "holiday_allowance, password, role) " +
                "VALUES ('Admin', 'User', 'admin@example.com', 'IT', 60000.00, '2024-01-01', 30, " +
                "'admin_password', 'Admin');");
        db.execSQL("INSERT INTO User (first_name, last_name, email, department, salary, start_date, " +
                "holiday_allowance, password, role) " +
                "VALUES ('John', 'Doe', 'john.doe@example.com', 'Engineering', 50000.00, '2024-01-01', " +
                "20, 'employee_password', 'Employee');");

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


    // Helper Methods for Mapping Cursors to Objects
    public Employee cursorToEmployee(Cursor cursor) {
        Log.d("cursorToEmployee", "Attempting to convert Cursor to Employee.");
        Employee employee = null;
        try {
            employee = new Employee(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("department")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("salary")),
                    cursor.getString(cursor.getColumnIndexOrThrow("start_date")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("holiday_allowance")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("role"))
            );
        } catch (Exception e) {
            Log.e("cursorToEmployee", "Failed to convert Cursor to Employee", e);
        }
        return employee;
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


    // Read Methods
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

    public Employee getEmployeeById(int userId) {
        Log.d("getEmployeeByID", "Attempting to get Employee #" + userId);
        Employee employee = null;
        Cursor cursor = null;
        try (SQLiteDatabase db = getReadableDatabase()) {
            cursor = db.rawQuery("SELECT * FROM User WHERE id = ?", new String[]{ Integer.toString(userId) });
            Log.d("getEmployeeByID", "Found " + cursor.getCount() + " employees.");
            if (cursor.moveToFirst()) {
                employee = cursorToEmployee(cursor);
            }
            Log.d("getEmployeeByID", "Converted Cursor to Employee.");
        } catch (Exception e) {
            Log.e("getEmployeeByID", "Failed to get Employee #" + userId);
        } finally {
            if (cursor != null) { cursor.close(); }
        }

        return employee;
    }

    public List<PtoRequest> getAllPtoRequests() {
        List<PtoRequest> ptoRequestList = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery("SELECT * FROM PtoRequest", null)) {
            if (cursor.moveToFirst()) {
                do {
                    ptoRequestList.add(cursorToPtoRequest(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error fetching PTO requests: " + e.getMessage());
        }

        return ptoRequestList;
    }

    public List<PtoRequest> getAllPtoRequestsByRequester(int requesterId) {
        List<PtoRequest> ptoRequestList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM PtoRequest WHERE requester_id = ?",
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


    // Create Methods
    public void insertUser(Context context, Employee newEmployee) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues userValues = new ContentValues();
        ContentValues settingsValues = new ContentValues();

        db.beginTransaction();
        try {

            if (newEmployee.getId() > 0) { // if ID is meaningful
                userValues.put("id", newEmployee.getId());
            }

            userValues.put("first_name", newEmployee.getFirstName());
            userValues.put("last_name", newEmployee.getLastName());
            userValues.put("email", newEmployee.getEmail());
            userValues.put("department", newEmployee.getDepartment()); // New field
            userValues.put("salary", newEmployee.getSalary()); // New field
            userValues.put("start_date", newEmployee.getStartDate());
            userValues.put("holiday_allowance", newEmployee.getHolidayAllowance());
            userValues.put("password", newEmployee.getPassword());
            userValues.put("role", newEmployee.getRole());

            long userId = db.insert("User", null, userValues);
            if (userId == -1) {
                throw new SQLException("Failed to insert new user.");
            }

            settingsValues.put("user_id", userId);
            settingsValues.put("pto_notifications", 1);
            settingsValues.put("details_notifications", 1);
            settingsValues.put("dark_theme", 1);
            settingsValues.put("red_green_theme", 0);

            long settingsId = db.insert("UserSettings", null, settingsValues);
            if (settingsId == -1) { throw new SQLException("Failed to insert new user's settings."); }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("InsertUser", "Failed to insert new user.");
            throw new SQLException("Failed to insert new user.");
        } finally {
            db.endTransaction();
            db.close();
        }

        List<Employee> updatedEmployeeList = getAllEmployees();
        EmployeeAdapter employeeAdapter = new EmployeeAdapter(context, updatedEmployeeList);
        employeeAdapter.notifyDataSetChanged();
    }


    // Update Methods
    public void updateUserInDatabase(Context context, Employee employee) {
        ContentValues values = new ContentValues();
        values.put("first_name", employee.getFirstName());
        values.put("last_name", employee.getLastName());
        values.put("email", employee.getEmail());
        values.put("department", employee.getDepartment());
        values.put("salary", employee.getSalary());
        values.put("start_date", employee.getStartDate());
        values.put("holiday_allowance", employee.getHolidayAllowance());
        values.put("password", employee.getPassword());
        values.put("role", employee.getRole());

        try (SQLiteDatabase db = getWritableDatabase()) {
            db.update("User", values, "id = ?",
                    new String[]{ Integer.toString(employee.getId()) });
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error updating employee details.");
            throw e;
        }

        List<Employee> updatedEmployeeList = getAllEmployees();
        EmployeeAdapter employeeAdapter = new EmployeeAdapter(context, updatedEmployeeList);
        employeeAdapter.notifyDataSetChanged();
    }


    // Delete Methods
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

    public void deleteEmployee(int id) {
        try (SQLiteDatabase db = getWritableDatabase()) { // AndroidStudio suggested this \_O_/
            db.delete("User", "id = ?", new String[]{Integer.toString(id)});
            db.delete("UserSettings", "user_id = ?", new String[]{Integer.toString(id)});
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error deleting employee " + id, e);
            throw e; // Propagates the error to PtoAdapter::cancelPtoRequest to Toast in context
        }
    }


    // Authentication and User Management Methods
    // Writes user to SharedPreferences if exists in database
    public boolean authenticateUser(Context context, String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE email = ? AND password = ?",
                new String[]{ email, password });

        if (cursor != null && cursor.moveToFirst()) {
            Employee employee = cursorToEmployee(cursor);
            saveCurrentUser(context, employee);

            Log.d("Login", "Login Succeeded, " + employee.getFullName() + ".");
            cursor.close();
            db.close();
            return true;
        } else {
            Log.d("Login", "Login Failed, User doesn't exist.");
            if (cursor != null) { cursor.close(); }
            db.close();
            return false;
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


    // User Settings Methods
    public UserSettings loadUserSettings(Employee currentUser) {

        UserSettings userSettings = null;
        Cursor cursor = null;

        try (SQLiteDatabase db = getReadableDatabase()) {
            String query = "SELECT pto_notifications, details_notifications, dark_theme, red_green_theme " +
                    "FROM UserSettings " +
                    "WHERE user_id = ?";

            cursor = db.rawQuery(query, new String[]{Integer.toString(currentUser.getId())});

            if (cursor != null && cursor.moveToFirst()) {
                int ptoIndex = cursor.getColumnIndex("pto_notifications");
                int detailsIndex = cursor.getColumnIndex("details_notifications");
                int darkThemeIndex = cursor.getColumnIndex("dark_theme");
                int redGreenIndex = cursor.getColumnIndex("red_green_theme");

                if (ptoIndex >= 0 && detailsIndex >= 0 && darkThemeIndex >= 0 && redGreenIndex >= 0) {
                    boolean ptoNotifications = cursor.getInt(ptoIndex) == 1;
                    boolean detailsNotifications = cursor.getInt(detailsIndex) == 1;
                    boolean darkTheme = cursor.getInt(darkThemeIndex) == 1;
                    boolean redGreenTheme = cursor.getInt(redGreenIndex) == 1;

                    userSettings = new UserSettings(ptoNotifications, detailsNotifications, darkTheme, redGreenTheme);
                }
            } else {
                throw new Exception("A UserSettings column was missing.");
            }
        } catch (Exception e) {
            // Return defaults
            Log.e("LoadUserSettings", "A UserSettings column was missing.");
            return new UserSettings(true, true, true, false);
        } finally {
            if (cursor != null) { cursor.close(); }
        }

        return userSettings;
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
}
