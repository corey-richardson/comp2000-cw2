package com.example.employeeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity {

    Employee currentUser;
    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.employeeDetailsButton).setVisibility(View.GONE);
        findViewById(R.id.employeePtoRequestsButton).setVisibility(View.GONE);
        findViewById(R.id.addEmployeeButton).setVisibility(View.GONE);

        currentUser = databaseHelper.loadCurrentUser(this);
        TextView employeeNameTextView = findViewById(R.id.employeeName);

        assert currentUser != null;
        if (currentUser.getRole().equals("Admin")) {
            findViewById(R.id.employeeDetailsButton).setVisibility(View.VISIBLE);
            findViewById(R.id.employeePtoRequestsButton).setVisibility(View.VISIBLE);
            findViewById(R.id.addEmployeeButton).setVisibility(View.VISIBLE);
            employeeNameTextView.setText(String.format("%s [Admin]", currentUser.getFull_name()));
        } else {
            employeeNameTextView.setText(currentUser.getFull_name());
        }
    }

    public void launchEmployeeDetails(View v)
    {
        Intent iLaunchEmployeeDetails = new Intent(this, EditPersonalDetails.class);
        startActivity(iLaunchEmployeeDetails);
    }

    public void launchHolidayRequests(View v)
    {
        Intent iLaunchHolidayRequests = new Intent(this, ptoMenu.class);
        startActivity(iLaunchHolidayRequests);
    }

    public void launchSettings(View v)
    {
        Intent iLaunchSettings = new Intent(this, Settings.class);
        startActivity(iLaunchSettings);
    }

    public void launchAEmployeeDetails(View v)
    {
        Intent iLaunchAEmployeeDetails = new Intent(this, aEmployeeDetails.class);
        startActivity(iLaunchAEmployeeDetails);
    }

    public void launchPtoRequests(View v)
    {
        Intent iLaunchPtoRequests = new Intent(this, aPtoRequests.class);
        startActivity(iLaunchPtoRequests);
    }

    public void launchAddEmployee(View v)
    {
        Intent iLaunchAddEmployee = new Intent(this, aAddEmployee.class);
        startActivity(iLaunchAddEmployee);
    }

    public void handleLogout(View v)
    {
        Intent iLogout = new Intent(this, MainActivity.class);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.clearCurrentUser(this);

        // Clear the back stack to prevent going back to this activity
        iLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iLogout);
        finish();
    }
}