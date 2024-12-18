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
import android.widget.EditText;
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
        EditText editTextEmail = findViewById(R.id.loginScreenEmailField);
        EditText editTextPassword = findViewById(R.id.loginScreenPasswordField);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        databaseHelper.authenticateUser(this, email, password);

        Employee user = databaseHelper.loadCurrentUser(this);
        if (user == null)
        {
            return;
        }

        Intent iLaunchHomepage;
        if (user.getRole().equals("Admin"))
        {
            iLaunchHomepage = new Intent(this, AdminDashboard.class);
        }
        else {
            iLaunchHomepage = new Intent(this, EmployeeDashboard.class);
        }

        startActivity(iLaunchHomepage);

    }

    public void launchForgotPassword(View v)
    {
        Intent iLaunchForgotPassword = new Intent(this, ForgotPassword.class);
        startActivity(iLaunchForgotPassword);
    }
}
