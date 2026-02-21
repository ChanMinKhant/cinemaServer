package com.cinema.showtime;

import java.sql.Date;
import java.sql.Time;

public class Showtime {
    private Integer id;
    private int movieId;
    private String room;
    private Date showDate;
    private Time showTime;

    public Showtime() {}

    public Showtime(Integer id, int movieId, String room, Date showDate, Time showTime) {
        this.id = id;
        this.movieId = movieId;
        this.room = room;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public Date getShowDate() { return showDate; }
    public void setShowDate(Date showDate) { this.showDate = showDate; }
    public Time getShowTime() { return showTime; }
    public void setShowTime(Time showTime) { this.showTime = showTime; }
}