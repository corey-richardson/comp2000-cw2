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
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialise databaseHelper
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    public void handleLogin(View v)
    {
        // TEST CODE!
        // Does not yet handle authentication and accounts
        TextView t = findViewById(R.id.loginScreenEmailField);
        Intent iLaunchDashboard;

        Log.d("information", t.getText().toString());

        if (t.getText().toString().equals("admin"))
        {
            iLaunchDashboard = new Intent(this, AdminDashboard.class);
        }
        else
        {
            iLaunchDashboard = new Intent(this, EmployeeDashboard.class);
        }


        // Intent iLaunchDashboard = new Intent(this, EmployeeDashboard.class);
        startActivity(iLaunchDashboard);
    }

    public void launchForgotPassword(View v)
    {
        Intent iLaunchForgotPassword = new Intent(this, ForgotPassword.class);
        startActivity(iLaunchForgotPassword);
    }
}