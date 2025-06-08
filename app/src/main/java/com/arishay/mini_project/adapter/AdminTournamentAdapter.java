package com.arishay.mini_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Tournament;

import java.util.List;

public class AdminTournamentAdapter extends RecyclerView.Adapter<AdminTournamentAdapter.ViewHolder> {

    public interface AdminAction {
        void onEdit(Tournament tournament);
        void onDelete(Tournament tournament);
    }

    private List<Tournament> tournaments;
    private AdminAction action;

    public AdminTournamentAdapter(List<Tournament> tournaments, AdminAction action) {
        this.tournaments = tournaments;
        this.action = action;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, dateText;
        Button editBtn, deleteBtn;

        public ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.nameText);
            dateText = view.findViewById(R.id.dateText);
            editBtn = view.findViewById(R.id.editBtn);
            deleteBtn = view.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public AdminTournamentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tournament_item_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTournamentAdapter.ViewHolder holder, int position) {
        Tournament t = tournaments.get(position);
        holder.nameText.setText(t.name);
        holder.dateText.setText("From " + t.startDate + " to " + t.endDate);

        holder.editBtn.setOnClickListener(v -> action.onEdit(t));
        holder.deleteBtn.setOnClickListener(v -> action.onDelete(t));
    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }
}
