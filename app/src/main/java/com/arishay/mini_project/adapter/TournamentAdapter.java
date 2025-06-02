package com.arishay.mini_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Tournament;

import java.util.List;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {

    private List<Tournament> tournamentList;

    public TournamentAdapter(List<Tournament> tournamentList) {
        this.tournamentList = tournamentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tournament_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tournament t = tournamentList.get(position);
        holder.name.setText(t.name);
        holder.category.setText("Category: " + t.category);
        holder.difficulty.setText("Difficulty: " + t.difficulty);
        holder.dates.setText("From " + t.startDate + " to " + t.endDate);
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, difficulty, dates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tourName);
            category = itemView.findViewById(R.id.tourCategory);
            difficulty = itemView.findViewById(R.id.tourDifficulty);
            dates = itemView.findViewById(R.id.tourDates);
        }
    }
}
