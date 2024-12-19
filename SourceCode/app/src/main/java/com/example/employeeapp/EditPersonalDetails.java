package com.example.employeeapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Date;
import java.text.SimpleDateFormat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditPersonalDetails extends AppCompatActivity {

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


        currentUser = DatabaseHelper.loadCurrentUser(this);

        TextView employeeNameTextView = findViewById(R.id.employeeName);
        employeeNameTextView.setText(currentUser.getFull_name());

        Date startDate = currentUser.getStart_date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String startDateString = dateFormat.format(startDate);

        ((TextView) findViewById(R.id.employeeDetailsFirstNameField)).setText(currentUser.getFirst_name());
        ((TextView) findViewById(R.id.employeeDetailsLastNameField)).setText(currentUser.getLast_name());
        ((TextView) findViewById(R.id.employeeDetailsEmailField)).setText(currentUser.getEmail());
        ((TextView) findViewById(R.id.employeeDetailsPhoneField)).setText(currentUser.getPhone());
        ((TextView) findViewById(R.id.employeeDetailsAddressField)).setText(currentUser.getAddress());
        ((TextView) findViewById(R.id.employeeDetailsJobTitleField)).setText(currentUser.getJob_title());
        ((EditText) findViewById(R.id.employeeDetailsStartDateField)).setText(startDateString);
    }
}
