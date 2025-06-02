package com.arishay.mini_project.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.OpenTDBResponse;
import com.arishay.mini_project.model.Question;
import com.arishay.mini_project.model.Tournament;
import com.arishay.mini_project.network.OpenTDBService;
import com.arishay.mini_project.network.RetrofitClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateTournamentActivity extends AppCompatActivity {

    private EditText nameInput, categoryInput, difficultyInput, startDateInput, endDateInput;
    private Button saveBtn;
    private FirebaseFirestore db;
    private String selectedDifficulty = "medium"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_tournament);

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
        String difficulty = difficultyInput.getText().toString().toLowerCase();
        String startDate = startDateInput.getText().toString();
        String endDate = endDateInput.getText().toString();

        if (name.isEmpty() || category.isEmpty() || difficulty.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Tournament tournament = new Tournament(name, category, difficulty, startDate, endDate);

        db.collection("tournaments")
                .add(tournament)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tournament created", Toast.LENGTH_SHORT).show();
                    fetchQuestionsForTournament(documentReference.getId(), difficulty);
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchQuestionsForTournament(String tournamentId, String difficulty) {
        OpenTDBService service = RetrofitClient.getService();
        service.getQuestions(10, 9, difficulty, "mixed").enqueue(new Callback<OpenTDBResponse>() {
            @Override
            public void onResponse(Call<OpenTDBResponse> call, Response<OpenTDBResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Question> questions = response.body().results;

                    for (Question q : questions) {
                        db.collection("tournaments")
                                .document(tournamentId)
                                .collection("questions")
                                .add(q);
                    }

                    Toast.makeText(AdminCreateTournamentActivity.this, "Questions added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminCreateTournamentActivity.this, "Failed to fetch questions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OpenTDBResponse> call, Throwable t) {
                Toast.makeText(AdminCreateTournamentActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("OpenTDB", "Error", t);
            }
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
