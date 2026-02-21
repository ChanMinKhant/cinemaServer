package com.cinema.movie;

public class Movie {
    private Integer id;
    private String title;
    private int duration;
    private long income; // New field for fixed Myanmar Kyat

    public Movie() {}

    public Movie(Integer id, String title, int duration, long income) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.income = income;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public long getIncome() { return income; }
    public void setIncome(long income) { this.income = income; }
}