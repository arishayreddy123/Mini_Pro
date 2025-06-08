package com.arishay.mini_project.model;

import java.util.ArrayList;
import java.util.List;

public class Tournament {
    public String id;
    public String name;
    public String category;
    public String difficulty;
    public String startDate;
    public String endDate;

    public List<String> playedUserIds;
    public int likeCount;
    public List<String> likedUserIds;

    // Not stored in Firestore — runtime-only
    public boolean canPlay = false;

    // Required public constructor for Firestore
    public Tournament() {}

    public Tournament(String name, String category, String difficulty, String startDate, String endDate) {
        this.name = name;
        this.category = category;
        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.playedUserIds = new ArrayList<>();
        this.likedUserIds = new ArrayList<>();
        this.likeCount = 0;
    }
}
