package com.example.employeeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.app.DatePickerDialog;


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

public class EditPtoRequest extends AppCompatActivity {

    Employee currentUser;
    DatabaseHelper databaseHelper;
    TextView startDateEditDatetime, endDateEditDatetime;
    TextView commentEditText;
    int ptoRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pto_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            databaseHelper = DatabaseHelper.getInstance(this);
            currentUser = databaseHelper.loadCurrentUser(this);

            startDateEditDatetime = findViewById(R.id.ptoEditStartDatetime);
            endDateEditDatetime = findViewById(R.id.ptoEditEndDatetime);
            commentEditText = findViewById(R.id.ptoEditAddInfo);

            Intent intent = getIntent();
            ptoRequestId = intent.getIntExtra("ptoRequestId", -1); // defaults -1
            String startDate = intent.getStringExtra("startDate");
            String endDate = intent.getStringExtra("endDate");
            String requestComment = intent.getStringExtra("requestComment");

            startDateEditDatetime.setText(startDate);
            endDateEditDatetime.setText(endDate);
            commentEditText.setText(requestComment);

            startDateEditDatetime.setOnClickListener(view -> showDatePickerDialog(true));
            endDateEditDatetime.setOnClickListener(view -> showDatePickerDialog(false));

            return insets;
        });
    }

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
                    startDateEditDatetime.setText(formattedDateTime);
                } else {
                    endDateEditDatetime.setText(formattedDateTime);
                }
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Set minimum date
        datePickerDialog.getDatePicker().updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void handleSubmit(View v) {
        String startDate = startDateEditDatetime.getText().toString().trim();
        String endDate = endDateEditDatetime.getText().toString().trim();
        String requestComment = commentEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        values.put("request_comment", requestComment);

        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            db.update("PtoRequest", values, "id = ?", new String[]{ Integer.toString(ptoRequestId)} );
            Toast.makeText(this, "Updated PTO Request.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Log.e("EditPtoRequest", "Error editing PTO Request ", e);
            Toast.makeText(this, "Failed to edit PTO Request.", Toast.LENGTH_SHORT).show();
        }

        Intent iLaunchViewHoliday = new Intent(this, ViewHoliday.class);
        startActivity(iLaunchViewHoliday);
        finish();
    }
}
