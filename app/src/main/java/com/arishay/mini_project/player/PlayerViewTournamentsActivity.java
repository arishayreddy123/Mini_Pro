package com.arishay.mini_project.player;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.adapter.PlayerTournamentAdapter;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerViewTournamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView infoText;
    private Button backBtn;

    private PlayerTournamentAdapter adapter;
    private List<Tournament> tournamentList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String filterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view_tournaments);

        recyclerView = findViewById(R.id.recyclerView);
        infoText = findViewById(R.id.infoText);
        backBtn = findViewById(R.id.backBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerTournamentAdapter(this, tournamentList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        filterType = getIntent().getStringExtra("filterType");
        loadTournaments(filterType);

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadTournaments(String type) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("tournaments").get()
                .addOnSuccessListener(query -> {
                    tournamentList.clear();
                    Date today = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Tournament t = doc.toObject(Tournament.class);
                        if (t == null || t.startDate == null || t.endDate == null) continue;

                        t.id = doc.getId();
                        boolean hasPlayed = t.playedUserIds != null && t.playedUserIds.contains(userId);

                        try {
                            Date start = sdf.parse(t.startDate);
                            Date end = sdf.parse(t.endDate);

                            switch (type.toLowerCase()) {
                                case "ongoing":
                                    if (!today.before(start) && !today.after(end) && !hasPlayed)
                                        tournamentList.add(t);
                                    break;
                                case "upcoming":
                                    if (today.before(start))
                                        tournamentList.add(t);
                                    break;
                                case "past":
                                    if (today.after(end))
                                        tournamentList.add(t);
                                    break;
                                case "participated":
                                    if (hasPlayed)
                                        tournamentList.add(t);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                    infoText.setVisibility(tournamentList.isEmpty() ? View.VISIBLE : View.GONE);
                    infoText.setText("No " + type + " tournaments found.");
                });
    }
}
