package com.example.employeeapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.util.Patterns;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailField = findViewById(R.id.forgotEmailField);

        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                findViewById(R.id.forgotPasswordLabel).setVisibility(View.INVISIBLE);
                findViewById(R.id.forgotNewPasswordField).setVisibility(View.INVISIBLE);
                findViewById(R.id.forgotConfirmNewPasswordLabel).setVisibility(View.INVISIBLE);
                findViewById(R.id.forgotConfirmNewPasswordField).setVisibility(View.INVISIBLE);
                findViewById(R.id.forgotSubmit).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Optional: Handle live changes if needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches())
                {
                    findViewById(R.id.forgotPasswordLabel).setVisibility(View.VISIBLE);
                    findViewById(R.id.forgotNewPasswordField).setVisibility(View.VISIBLE);
                    findViewById(R.id.forgotConfirmNewPasswordLabel).setVisibility(View.VISIBLE);
                    findViewById(R.id.forgotConfirmNewPasswordField).setVisibility(View.VISIBLE);
                    findViewById(R.id.forgotSubmit).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.forgotPasswordLabel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.forgotNewPasswordField).setVisibility(View.INVISIBLE);
                    findViewById(R.id.forgotConfirmNewPasswordLabel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.forgotConfirmNewPasswordField).setVisibility(View.INVISIBLE);
                    findViewById(R.id.forgotSubmit).setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}