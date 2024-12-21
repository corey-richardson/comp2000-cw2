package com.example.employeeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
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

public class RequestPTO extends AppCompatActivity {

    Employee currentUser;
    DatabaseHelper databaseHelper;

    TextView ptoStartDatetime, ptoEndDatetime;
    TextView ptoCommentText;

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

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);

        ptoStartDatetime = findViewById(R.id.ptoStartDatetime);
        ptoEndDatetime = findViewById(R.id.ptoEndDatetime);
        ptoCommentText = findViewById(R.id.ptoAddInfo);

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
        String startDate = ptoStartDatetime.getText().toString().trim();
        String endDate = ptoEndDatetime.getText().toString().trim();
        String requestComment = ptoCommentText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put("requester_id", currentUser.getId());
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        values.put("status", "Waiting");
        values.put("request_comment", requestComment);

        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            long result = db.insert("PtoRequest", null, values);
            Log.e("RequestPTO", Long.toString(result));

            if (result == -1) {
                Toast.makeText(this, "Failed to submit PTO Request.", Toast.LENGTH_SHORT).show();
                // throw new SQLiteConstraintException("Possible duplicate PTO request for these dates; manually thrown!");
                return;
            }

            Toast.makeText(this, "Submitted PTO Request.", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            if (e instanceof SQLiteConstraintException) {
                Toast.makeText(this, "You have already submitted a PTO request for these dates.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to submit PTO Request.", Toast.LENGTH_SHORT).show();
            }
            Log.e("DatabaseError", "Error while submitting PTO request", e);
            return;
        }

        Intent iLaunchViewHoliday = new Intent(this, ViewHoliday.class);
        startActivity(iLaunchViewHoliday);
        finish();
    }
}
