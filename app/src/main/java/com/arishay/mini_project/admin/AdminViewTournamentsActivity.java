package com.arishay.mini_project.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arishay.mini_project.R;
import com.arishay.mini_project.adapter.AdminTournamentAdapter;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.*;

import java.util.*;

public class AdminViewTournamentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminTournamentAdapter adapter;
    private List<Tournament> tournamentList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_tournaments);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        adapter = new AdminTournamentAdapter(tournamentList, new AdminTournamentAdapter.AdminAction() {
            @Override
            public void onEdit(Tournament t) {
                editTournament(t);
            }

            @Override
            public void onDelete(Tournament t) {
                deleteTournament(t);
            }
        });

        recyclerView.setAdapter(adapter);

        loadTournaments();
    }

    private void loadTournaments() {
        db.collection("tournaments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tournamentList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tournament t = doc.toObject(Tournament.class);
                        if (t != null) {
                            t.id = doc.getId();
                            tournamentList.add(t);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void editTournament(Tournament t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Tournament");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Tournament Name");
        nameInput.setText(t.name);
        layout.addView(nameInput);

        final EditText startDate = new EditText(this);
        startDate.setHint("Start Date (yyyy-MM-dd)");
        startDate.setText(t.startDate);
        layout.addView(startDate);

        final EditText endDate = new EditText(this);
        endDate.setHint("End Date (yyyy-MM-dd)");
        endDate.setText(t.endDate);
        layout.addView(endDate);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", nameInput.getText().toString());
            updates.put("startDate", startDate.getText().toString());
            updates.put("endDate", endDate.getText().toString());

            db.collection("tournaments").document(t.id)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                        loadTournaments();
                    });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
                                loadTournaments();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
