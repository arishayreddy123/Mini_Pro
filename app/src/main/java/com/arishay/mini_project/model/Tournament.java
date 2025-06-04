package com.arishay.mini_project.model;

public class Tournament {
    public String id;
    public String name;
    public String category;
    public String difficulty;
    public String startDate;
    public String endDate;
    public Integer likes; // ✅ use Integer to allow null handling

    // Required empty constructor for Firestore
    public Tournament() {}

    public Tournament(String name, String category, String difficulty, String startDate, String endDate) {
        this.name = name;
        this.category = category;
        this.difficulty = difficulty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.likes = 0;
    }
}
