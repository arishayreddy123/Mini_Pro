package com.arishay.mini_project.player;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.adapter.PlayerTournamentAdapter;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.*;

import java.util.*;

public class PlayerViewTournamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlayerTournamentAdapter adapter;
    private List<Tournament> tournamentList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // or your custom layout

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        adapter = new PlayerTournamentAdapter(this, tournamentList);
        recyclerView.setAdapter(adapter);

        loadTournaments();
    }

    private void loadTournaments() {
        db.collection("tournaments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tournamentList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tournament t = doc.toObject(Tournament.class);
                        t.id = doc.getId(); // 👈 store Firestore doc ID
                        tournamentList.add(t);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
