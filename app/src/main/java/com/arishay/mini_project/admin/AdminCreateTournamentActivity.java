package com.arishay.mini_project.admin;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminCreateTournamentActivity extends AppCompatActivity {

    private EditText nameInput, categoryInput, difficultyInput, startDateInput, endDateInput;
    private Button saveBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_tournament);

        // Bind UI elements
        nameInput = findViewById(R.id.nameInput);
        categoryInput = findViewById(R.id.categoryInput);
        difficultyInput = findViewById(R.id.difficultyInput);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        saveBtn = findViewById(R.id.saveBtn);

        db = FirebaseFirestore.getInstance();

        saveBtn.setOnClickListener(v -> saveTournament());
    }

    private void saveTournament() {
        String name = nameInput.getText().toString();
        String category = categoryInput.getText().toString();
        String difficulty = difficultyInput.getText().toString();
        String startDate = startDateInput.getText().toString();
        String endDate = endDateInput.getText().toString();

        if (name.isEmpty() || category.isEmpty() || difficulty.isEmpty()
                || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Tournament tournament = new Tournament(name, category, difficulty, startDate, endDate);

        db.collection("tournaments")
                .add(tournament)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tournament saved", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearFields() {
        nameInput.setText("");
        categoryInput.setText("");
        difficultyInput.setText("");
        startDateInput.setText("");
        endDateInput.setText("");
    }
}
