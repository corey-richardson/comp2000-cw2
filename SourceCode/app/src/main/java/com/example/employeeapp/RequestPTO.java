package com.example.employeeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.Toast;

public class RequestPTO extends AppCompatActivity {

    Employee currentUser;
    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);

    private TextView ptoStartDatetime;
    private TextView ptoEndDatetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_pto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentUser = databaseHelper.loadCurrentUser(this);

        ptoStartDatetime = findViewById(R.id.ptoStartDatetime);
        ptoEndDatetime = findViewById(R.id.ptoEndDatetime);

        ptoStartDatetime.setOnClickListener(view -> showDatePickerDialog(true));
        ptoEndDatetime.setOnClickListener(view -> showDatePickerDialog(false));
    }


    // A DatePickerDialog was used to standardise the input into ISO8601
    // https://developer.android.com/reference/android/app/DatePickerDialog
    private void showDatePickerDialog(final boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
            // Format selected date and time
            calendar.set(year, month, dayOfMonth);

            Calendar currentDate = Calendar.getInstance();
            // Set these to start of day to allow currentDate to be selected in the Dialog
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            if (calendar.before(currentDate)) {
                Toast.makeText(this, "Selected date is in the past.", Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDateTime = dateFormat.format(calendar.getTime());

                if (isStartDate) {
                    ptoStartDatetime.setText(formattedDateTime);
                } else {
                    ptoEndDatetime.setText(formattedDateTime);
                }
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Set minimum date
        datePickerDialog.getDatePicker().updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void handleSubmit(View v) {
        EditText ptoRequestCommentField = findViewById(R.id.ptoAddInfo);

        String ptoStart = ptoStartDatetime.getText().toString().trim();
        String ptoEnd = ptoEndDatetime.getText().toString().trim();
        String ptoRequestComment = ptoRequestCommentField.getText().toString().trim();

        Log.d("DateFormatCheck", ptoStart);
        Log.d("DateFormatCheck", ptoEnd);
    }
}
