package com.arishay.mini_project.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arishay.mini_project.R;
import com.arishay.mini_project.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button createBtn, viewBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        welcomeText = findViewById(R.id.welcomeText);
        createBtn = findViewById(R.id.createBtn);
        viewBtn = findViewById(R.id.viewBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            welcomeText.setText("Welcome, " + user.getEmail());
        }

        createBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCreateTournamentActivity.class)));

        viewBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminViewTournamentsActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
