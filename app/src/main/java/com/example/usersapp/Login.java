package com.example.usersapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Login extends AppCompatActivity {
TextInputLayout log_email, log_pass;
Button login_btn;
DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        log_email = findViewById(R.id.log_email);
        log_pass = findViewById(R.id.log_pass);
        login_btn = findViewById(R.id.login_btn);
        dbHelper = new DbHelper(this);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Objects.requireNonNull(log_email.getEditText()).getText().toString().trim();
                String pass = Objects.requireNonNull(log_pass.getEditText()).getText().toString();
                if (email.isEmpty()) {
                    log_email.setError("This field is required");
                }
                if (pass.isEmpty()) {
                    log_pass.setError("This field is required");
                }
                else {
                    boolean loggedin = dbHelper.loginUserHelper(email, pass);
                    if (loggedin) {
                        Toast.makeText(getApplicationContext(), "login success!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Profile.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Login Failed...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}