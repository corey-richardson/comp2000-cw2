package com.example.employeeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

public class EmployeeDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

    public void handleLogout(View v)
    {
        Intent iLogout = new Intent(this, MainActivity.class);

        // Clear the back stack to prevent going back to this activity
        iLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iLogout);
        finish();
    }
}
