package com.arishay.mini_project.player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.arishay.mini_project.R;
import com.google.firebase.auth.FirebaseAuth;

public class PlayerHomeActivity extends AppCompatActivity {

    private Button logoutBtn, btnOngoing, btnUpcoming, btnPast, btnParticipated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);

        logoutBtn = findViewById(R.id.logoutBtn);
        btnOngoing = findViewById(R.id.btnOngoing);
        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnPast = findViewById(R.id.btnPast);
        btnParticipated = findViewById(R.id.btnParticipated);

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });

        btnOngoing.setOnClickListener(v -> openTournamentView("ongoing"));
        btnUpcoming.setOnClickListener(v -> openTournamentView("upcoming"));
        btnPast.setOnClickListener(v -> openTournamentView("past"));
        btnParticipated.setOnClickListener(v -> openTournamentView("participated"));
    }

    private void openTournamentView(String filterType) {
        Intent intent = new Intent(this, PlayerViewTournamentsActivity.class);
        intent.putExtra("filterType", filterType);
        startActivity(intent);
    }
}
