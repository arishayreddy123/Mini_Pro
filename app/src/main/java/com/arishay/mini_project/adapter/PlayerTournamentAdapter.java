package com.arishay.mini_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Tournament;
import com.arishay.mini_project.player.PlayerQuizActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class PlayerTournamentAdapter extends RecyclerView.Adapter<PlayerTournamentAdapter.ViewHolder> {

    private List<Tournament> tournamentList;
    private Context context;

    public PlayerTournamentAdapter(Context context, List<Tournament> list) {
        this.context = context;
        this.tournamentList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tournament_item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        Tournament t = tournamentList.get(pos);
        h.name.setText(t.name);
        h.category.setText("Category: " + t.category);
        h.difficulty.setText("Difficulty: " + t.difficulty);
        h.dates.setText(t.startDate + " to " + t.endDate);

        String uid = FirebaseAuth.getInstance().getUid();
        boolean hasPlayed = t.playedUserIds != null && t.playedUserIds.contains(uid);

        h.playBtn.setEnabled(!hasPlayed);
        h.playBtn.setText(hasPlayed ? "Already Played" : "Play");

        h.playBtn.setOnClickListener(v -> {
            Intent i = new Intent(context, PlayerQuizActivity.class);
            i.putExtra("tournamentId", t.id);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, difficulty, dates;
        Button playBtn;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tourName);
            category = itemView.findViewById(R.id.tourCategory);
            difficulty = itemView.findViewById(R.id.tourDifficulty);
            dates = itemView.findViewById(R.id.tourDates);
            playBtn = itemView.findViewById(R.id.playBtn);
        }
    }
}
