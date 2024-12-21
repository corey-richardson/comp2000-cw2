package com.example.employeeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;
import android.content.Intent;

public class EditPersonalDetails extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_personal_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        TextView employeeNameTextView = findViewById(R.id.employeeName);
        employeeNameTextView.setText(currentUser.getFullName());

        ((EditText) findViewById(R.id.employeeDetailsFirstNameField)).setText(currentUser.getFirstName());
        ((EditText) findViewById(R.id.employeeDetailsLastNameField)).setText(currentUser.getLastName());
        ((EditText) findViewById(R.id.employeeDetailsEmailField)).setText(currentUser.getEmail());
        ((EditText) findViewById(R.id.employeeDetailsDepartmentField)).setText(currentUser.getDepartment());
        ((EditText) findViewById(R.id.employeeDetailsSalaryField)).setText(getString(R.string.salary_string, currentUser.getSalary()));
        ((EditText) findViewById(R.id.employeeDetailsStartDateField)).setText(currentUser.getStartDate());
    }

    public void handleSaveDetails(View v) {
        String firstName = ((EditText) findViewById(R.id.employeeDetailsFirstNameField)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.employeeDetailsLastNameField)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.employeeDetailsEmailField)).getText().toString().trim();

        // Not editable:
        // currentUser.setJob_title(((EditText) findViewById(R.id.employeeDetailsJobTitleField)).getText().toString().trim());
        // currentUser.setStart_date(((EditText) findViewById(R.id.employeeDetailsStartDateField)).getText().toString().trim());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);

        // Update currentUser details saved in SharedPreferences and SQL Database to persist
        databaseHelper.saveCurrentUser(this, currentUser);
        databaseHelper.updateUserInDatabase(this, currentUser);
        Toast.makeText(this, "Employee details updated successfully", Toast.LENGTH_SHORT).show();

        Intent returnToDashboard = new Intent(EditPersonalDetails.this, Dashboard.class);
        startActivity(returnToDashboard);
        finish();
    }
}
