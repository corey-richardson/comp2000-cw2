package com.example.employeeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Employee currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        databaseHelper = DatabaseHelper.getInstance(this);
        currentUser = databaseHelper.loadCurrentUser(this);
        UserSettings currentUserUserSettings = databaseHelper.loadUserSettings(currentUser);

        TextView employeeName = findViewById(R.id.employeeNameSettings);
        employeeName.setText(currentUser .getFullName());

        Switch ptoRequestsSwitch = findViewById(R.id.switchPtoRequests);
        Switch detailsUpdatedSwitch = findViewById(R.id.switchDetailsUpdated);
        Switch darkThemeSwitch = findViewById(R.id.switchDarkMode);
        Switch redGreenThemeSwitch = findViewById(R.id.switchRedGreenColourblind);

        ptoRequestsSwitch.setChecked(currentUserUserSettings.getPtoNotifications());
        detailsUpdatedSwitch.setChecked(currentUserUserSettings.getDetailsNotifications());
        darkThemeSwitch.setChecked(currentUserUserSettings.getDarkTheme());
        redGreenThemeSwitch.setChecked(currentUserUserSettings.getRedGreenTheme());

        ptoRequestsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUserUserSettings.setPtoNotifications(isChecked);
            databaseHelper.updateUserSettings(currentUser.getId(), currentUserUserSettings);
            Log.d("UpdateSettings", "ptoRequestsSwitch");
        });

        detailsUpdatedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUserUserSettings.setDetailsNotifications(isChecked);
            databaseHelper.updateUserSettings(currentUser.getId(), currentUserUserSettings);
            Log.d("UpdateSettings", "detailsUpdatedSwitch");
        });

        darkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUserUserSettings.setDarkTheme(isChecked);
            databaseHelper.updateUserSettings(currentUser.getId(), currentUserUserSettings);
            Log.d("UpdateSettings", "darkThemeSwitch");
        });

        redGreenThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUserUserSettings.setRedGreenTheme(isChecked);
            databaseHelper.updateUserSettings(currentUser.getId(), currentUserUserSettings);
            Log.d("UpdateSettings", "redGreenThemeSwitch");
        });
    }
}
