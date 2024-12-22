package com.example.employeeapp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class aPtoRequests extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;
    aPtoAdapter aPtoAdapter;
    ListView aPtoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apto_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        aPtoListView = findViewById(R.id.aptoListView);
        List<PtoRequest> ptoRequestList = databaseHelper.getAllPtoRequests();
        aPtoAdapter = new aPtoAdapter(this, ptoRequestList);
        aPtoListView.setAdapter(aPtoAdapter);
    }
}