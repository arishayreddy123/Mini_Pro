package com.arishay.mini_project.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.adapter.TournamentAdapter;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class AdminViewTournamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TournamentAdapter adapter;
    private List<Tournament> tournamentList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Or create new layout if needed

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tournamentList = new ArrayList<>();
        adapter = new TournamentAdapter(tournamentList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchTournaments();
    }

    private void fetchTournaments() {
        db.collection("tournaments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tournamentList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tournament t = doc.toObject(Tournament.class);
                        tournamentList.add(t);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}
