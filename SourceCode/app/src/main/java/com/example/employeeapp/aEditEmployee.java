package com.example.employeeapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class aEditEmployee extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;

    EditText firstNameField, lastNameField, emailField, departmentField, salaryField;
    TextView startDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aedit_employee);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        firstNameField = findViewById(R.id.edit_first_name);
        lastNameField = findViewById(R.id.edit_last_name);
        emailField = findViewById(R.id.edit_email_address);
        departmentField = findViewById(R.id.edit_department);
        salaryField = findViewById(R.id.edit_salary);
        startDateField = findViewById(R.id.edit_start_date);

        Intent intent = getIntent();
        int idToEdit = intent.getIntExtra("employeeId", -1);

        if (idToEdit == -1) {
            Log.e("aEditEmployee", "Intent was not passed Employee ID to update.");
            finish();
            return;
        }

        Employee employeeToEdit = null;
        try {
            employeeToEdit = databaseHelper.getEmployeeById(idToEdit);
        } catch (Exception e) {
            Log.e("aEditEmployee", "No Employee found with ID " + idToEdit);
            finish();
            return;
        }

        TextView employeeNameTextView = findViewById(R.id.employeeName);
        employeeNameTextView.setText(employeeToEdit.getFullName());

        firstNameField.setText(employeeToEdit.getFirstName());
        lastNameField.setText(employeeToEdit.getLastName());
        emailField.setText(employeeToEdit.getEmail());
        departmentField.setText(employeeToEdit.getDepartment());
        salaryField.setText(getString(R.string.salary_string, employeeToEdit.getSalary()));
        startDateField.setText(employeeToEdit.getStartDate());

        startDateField.setOnClickListener(view -> showDatePickerDialog());
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