package com.arishay.mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.arishay.mini_project.auth.LoginActivity;
import com.arishay.mini_project.auth.RegisterActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
        });

        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
        });
    }
}
