package com.example.employeeapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ListView;
import java.util.List;

import android.util.Log;

public class ViewHoliday extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;
    PtoAdapter ptoAdapter;
    ListView ptoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_holiday);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        ptoListView = findViewById(R.id.ptoListView);
        List<PtoRequest> ptoRequestList = databaseHelper.getAllPtoRequests();
        ptoAdapter = new PtoAdapter(this, ptoRequestList);
        ptoListView.setAdapter(ptoAdapter);
    }
}
