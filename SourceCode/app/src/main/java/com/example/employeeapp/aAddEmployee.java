package com.example.employeeapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class aAddEmployee extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;
    TextView startDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aadd_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        startDateField = findViewById(R.id.addEmployeeStartDateField);
        startDateField.setOnClickListener(view -> showDatePickerDialog());
    }


    public void saveNewEmployee(View v) {
        String firstName = ((EditText) findViewById(R.id.addEmployeeFirstNameField)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.addEmployeeLastNameField)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.addEmployeeEmailField)).getText().toString().trim();
        String department = ((EditText) findViewById(R.id.addEmployeeDepartmentField)).getText().toString().trim();
        String salaryString = ((EditText) findViewById(R.id.addEmployeeSalaryField)).getText().toString().trim();
        float salary = Float.parseFloat(salaryString);
        String startDate = startDateField.getText().toString().trim();

        Employee newEmployee = new Employee(-1, firstName, lastName, email, department,
                salary, startDate, 30, "new_starter_password", "Employee");

        // Save the new employee LOCALLY
//        try {
//            databaseHelper.insertUser(newEmployee);
//            Toast.makeText(this, "Added new employee to database.", Toast.LENGTH_SHORT).show();
//            Intent iLaunchViewEmployees = new Intent(this, aEmployeeDetails.class);
//            startActivity(iLaunchViewEmployees);
//            finish();
//        } catch (SQLException e) {
//            Toast.makeText(this, "Failed to add new employee.", Toast.LENGTH_SHORT).show();
//        }

        // NOW USE THE API
        try {
            ApiService.apiInsertUser(this, newEmployee);
            Toast.makeText(this, "Uploaded via API!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to upload the employee via the API.", Toast.LENGTH_SHORT).show();
        }

        // Re-fetch from API to update local database
        ApiService.fetchAndStoreEmployees(this);

        Intent iLaunchViewEmployees = new Intent(this, aEmployeeDetails.class);
        startActivity(iLaunchViewEmployees);
        finish();
    }

    // A DatePickerDialog was used to standardise the input into ISO8601
    // https://developer.android.com/reference/android/app/DatePickerDialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
            // Format selected date and time
            calendar.set(year, month, dayOfMonth);

            Calendar currentDate = Calendar.getInstance();
            // Set these to start of day to allow currentDate to be selected in the Dialog
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDateTime = dateFormat.format(calendar.getTime());
            startDateField.setText(formattedDateTime);

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
