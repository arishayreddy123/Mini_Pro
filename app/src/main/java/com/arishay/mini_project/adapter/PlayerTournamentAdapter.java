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

import java.util.List;

public class PlayerTournamentAdapter extends RecyclerView.Adapter<PlayerTournamentAdapter.ViewHolder> {

    private Context context;
    private List<Tournament> tournamentList;

    public PlayerTournamentAdapter(Context context, List<Tournament> tournamentList) {
        this.context = context;
        this.tournamentList = tournamentList;
    }

    @NonNull
    @Override
    public PlayerTournamentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tournament_item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerTournamentAdapter.ViewHolder holder, int position) {
        Tournament t = tournamentList.get(position);

        holder.name.setText(t.name);
        holder.category.setText("Category: " + t.category);
        holder.difficulty.setText("Difficulty: " + t.difficulty);
        holder.dates.setText("From " + t.startDate + " to " + t.endDate);

        holder.playBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerQuizActivity.class);
            intent.putExtra("tournamentId", t.id); // pass ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, difficulty, dates;
        Button playBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tourName);
            category = itemView.findViewById(R.id.tourCategory);
            difficulty = itemView.findViewById(R.id.tourDifficulty);
            dates = itemView.findViewById(R.id.tourDates);
            playBtn = itemView.findViewById(R.id.playBtn);
        }
    }
}
