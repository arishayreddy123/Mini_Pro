package com.arishay.mini_project.player;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.adapter.PlayerTournamentAdapter;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerViewTournamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button backBtn;
    private PlayerTournamentAdapter adapter;
    private List<Tournament> tournamentList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view_tournaments);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backBtn = findViewById(R.id.backBtn);

        db = FirebaseFirestore.getInstance();
        adapter = new PlayerTournamentAdapter(this, tournamentList);
        recyclerView.setAdapter(adapter);

        loadTournaments();

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadTournaments() {
        db.collection("tournaments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tournamentList.clear();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date today = new Date();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tournament t = doc.toObject(Tournament.class);
                        if (t == null) continue;
                        t.id = doc.getId();

                        try {
                            Date start = sdf.parse(t.startDate);
                            Date end = sdf.parse(t.endDate);
                            if (start != null && end != null) {
                                if (!today.before(start) && !today.after(end)) {
                                    tournamentList.add(t);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
