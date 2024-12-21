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
        ((EditText) findViewById(R.id.employeeDetailsPhoneField)).setText(currentUser.getPhone());
        ((EditText) findViewById(R.id.employeeDetailsAddressField)).setText(currentUser.getAddress());
        ((EditText) findViewById(R.id.employeeDetailsJobTitleField)).setText(currentUser.getJob_title());
        ((EditText) findViewById(R.id.employeeDetailsStartDateField)).setText(currentUser.getStart_date());
    }

    public void handleSaveDetails(View v) {
        String firstName = ((EditText) findViewById(R.id.employeeDetailsFirstNameField)).getText().toString().trim();
        String lastName = ((EditText) findViewById(R.id.employeeDetailsLastNameField)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.employeeDetailsEmailField)).getText().toString().trim();
        String phone = ((EditText) findViewById(R.id.employeeDetailsPhoneField)).getText().toString().trim();
        String address = ((EditText) findViewById(R.id.employeeDetailsAddressField)).getText().toString().trim();

        // Not editable:
        // currentUser.setJob_title(((EditText) findViewById(R.id.employeeDetailsJobTitleField)).getText().toString().trim());
        // currentUser.setStart_date(((EditText) findViewById(R.id.employeeDetailsStartDateField)).getText().toString().trim());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        // Update currentUser details saved in SharedPreferences and SQL Database to persist
        databaseHelper.saveCurrentUser(this, currentUser);
        databaseHelper.updateUserInDatabase(this, currentUser);
        Toast.makeText(this, "Employee details updated successfully", Toast.LENGTH_SHORT).show();

        Intent returnToDashboard = new Intent(EditPersonalDetails.this, Dashboard.class);
        startActivity(returnToDashboard);
        finish();
    }
}
