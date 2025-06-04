package com.arishay.mini_project.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.adapter.AdminTournamentAdapter;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminViewTournamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminTournamentAdapter adapter;
    private List<Tournament> tournamentList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button backBtn;
    private Spinner filterSpinner;

    private final String[] filters = {"All", "Ongoing", "Upcoming", "Past"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_tournaments);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminTournamentAdapter(tournamentList,
                this::editTournament,
                this::deleteTournament);
        recyclerView.setAdapter(adapter);

        filterSpinner = findViewById(R.id.filterSpinner);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, filters);
        filterSpinner.setAdapter(filterAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                loadTournaments(filters[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadTournaments(String filterType) {
        db.collection("tournaments")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tournamentList.clear();
                    Date today = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (DocumentSnapshot doc : querySnapshot) {
                        Tournament t = doc.toObject(Tournament.class);
                        if (t == null) continue;

                        t.id = doc.getId();
                        if (t.likes == null) t.likes = 0;

                        try {
                            Date start = sdf.parse(t.startDate);
                            Date end = sdf.parse(t.endDate);

                            if ("All".equals(filterType)) {
                                tournamentList.add(t);
                            } else if ("Ongoing".equals(filterType)) {
                                if (start != null && end != null && !today.before(start) && !today.after(end)) {
                                    tournamentList.add(t);
                                }
                            } else if ("Upcoming".equals(filterType)) {
                                if (start != null && today.before(start)) {
                                    tournamentList.add(t);
                                }
                            } else if ("Past".equals(filterType)) {
                                if (end != null && today.after(end)) {
                                    tournamentList.add(t);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading tournaments", Toast.LENGTH_SHORT).show());
    }

    private void editTournament(Tournament t) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", t.name + " (Updated)");
        updates.put("startDate", t.startDate);
        updates.put("endDate", t.endDate);

        db.collection("tournaments").document(t.id)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Tournament updated", Toast.LENGTH_SHORT).show();
                    loadTournaments(filterSpinner.getSelectedItem().toString());
                });
    }

    private void deleteTournament(Tournament t) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tournament")
                .setMessage("Are you sure you want to delete this tournament?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("tournaments").document(t.id)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                loadTournaments(filterSpinner.getSelectedItem().toString());
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
