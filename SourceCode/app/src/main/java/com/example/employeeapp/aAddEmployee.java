package com.example.employeeapp;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class aAddEmployee extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;

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
    }

    public void saveNewEmployee(View v) {
        String firstName = ((EditText) findViewById(R.id.addEmployeeFirstNameField)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.addEmployeeLastNameField)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.addEmployeeEmailField)).getText().toString().trim();
        String phone = ((EditText) findViewById(R.id.addEmployeePhoneField)).getText().toString().trim();
        String address = ((EditText) findViewById(R.id.addEmployeeAddressField)).getText().toString().trim();
        String jobTitle = ((EditText) findViewById(R.id.addEmployeeJobTitleField)).getText().toString().trim();
        // String department = ((EditText) findViewById(R.id.addEmployeeDepartmentField)).getText().toString().trim();
        // Float salary = ((EditText) findViewById(R.id.addEmployeeSalaryField)).getText().toString().trim();
        String startDate = ((EditText) findViewById(R.id.addEmployeeStartDateField)).getText().toString().trim();

        Employee newEmployee = new Employee(-1, firstName, lastName, email, phone, address,
                jobTitle, startDate, "new_starter_password", 30, "Employee");

        try {
            databaseHelper.insertUser(newEmployee);
            Toast.makeText(this, "Added new employee to database.", Toast.LENGTH_SHORT).show();
            Intent iLaunchViewEmployees = new Intent(this, aEmployeeDetails.class);
            startActivity(iLaunchViewEmployees);
            finish();
        } catch (SQLException e) {
            Toast.makeText(this, "Failed to add new employee.", Toast.LENGTH_SHORT).show();
        }

        // NOW USE THE API
    }
}
