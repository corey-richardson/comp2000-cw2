package com.example.employeeapp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class aEmployeeDetails extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;
    EmployeeAdapter employeeAdapter;
    ListView employeeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aemployee_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        employeeListView = findViewById(R.id.employeeListView);
        List<Employee> employeeList = databaseHelper.getAllEmployees();
        employeeAdapter = new EmployeeAdapter(this, employeeList);
        employeeListView.setAdapter(employeeAdapter);
    }
}