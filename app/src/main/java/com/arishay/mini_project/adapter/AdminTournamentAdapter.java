package com.arishay.mini_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arishay.mini_project.R;
import com.arishay.mini_project.model.Tournament;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminTournamentAdapter extends RecyclerView.Adapter<AdminTournamentAdapter.ViewHolder> {

    public interface OnEditClickListener {
        void onEdit(Tournament tournament);
    }

    public interface OnDeleteClickListener {
        void onDelete(Tournament tournament);
    }

    private List<Tournament> tournamentList;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminTournamentAdapter(List<Tournament> tournamentList,
                                  OnEditClickListener editListener,
                                  OnDeleteClickListener deleteListener) {
        this.tournamentList = tournamentList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
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
        Tournament tournament = tournamentList.get(position);

        int currentLikes = (tournament.likes != null) ? tournament.likes : 0;

        holder.name.setText(tournament.name);
        holder.dates.setText(tournament.startDate + " - " + tournament.endDate);
        holder.likes.setText("Likes: " + currentLikes);

        holder.editBtn.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(tournament);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(tournament);
            }
        });

        holder.likeBtn.setOnClickListener(v -> {
            int updatedLikes = (currentLikes == 0) ? 1 : 0;
            tournament.likes = updatedLikes;
            db.collection("tournaments").document(tournament.id)
                    .update("likes", updatedLikes)
                    .addOnSuccessListener(unused -> {
                        holder.likes.setText("Likes: " + updatedLikes);
                        Toast.makeText(holder.itemView.getContext(),
                                "Likes updated to " + updatedLikes,
                                Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dates, likes;
        Button editBtn, deleteBtn, likeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tournamentName);
            dates = itemView.findViewById(R.id.tournamentDates);
            likes = itemView.findViewById(R.id.likesText);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
        }
    }
}
