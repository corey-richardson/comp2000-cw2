package com.example.employeeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialise databaseHelper
        databaseHelper = DatabaseHelper.getInstance(this);

        EditText editTextEmail = findViewById(R.id.loginScreenEmailField);
        EditText editTextPassword = findViewById(R.id.loginScreenPasswordField);

        editTextEmail.setText("admin@example.com");
        editTextPassword.setText("admin_password");

        ApiService.apiHealthCheck(this);
    }

    public void handleLogin(View v)
    {
        EditText editTextEmail = findViewById(R.id.loginScreenEmailField);
        EditText editTextPassword = findViewById(R.id.loginScreenPasswordField);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        boolean result = databaseHelper.authenticateUser(this, email, password);
        if (!result) {
            editTextPassword.setText("");
            Toast.makeText(this, "Incorrect email or password.", Toast.LENGTH_LONG).show();
        }

        Employee user = databaseHelper.loadCurrentUser(this);
        if (user == null)
        {
            return;
        }

        Intent iLaunchHomepage;
        iLaunchHomepage = new Intent(this, Dashboard.class);
        startActivity(iLaunchHomepage);

    }

    public void launchForgotPassword(View v)
    {
        Intent iLaunchForgotPassword = new Intent(this, ForgotPassword.class);
        startActivity(iLaunchForgotPassword);
    }
}
