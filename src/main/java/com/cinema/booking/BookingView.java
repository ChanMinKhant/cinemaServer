package com.cinema.booking;

import java.util.List;

public class BookingView {
    private int id;
    private int totalPrice;
    private long bookedAt;

    private String username;
    private String movieTitle;
    private String room;
    private String showDate;
    private String showTime;

    private List<String> seats;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public long getBookedAt() { return bookedAt; }
    public void setBookedAt(long bookedAt) { this.bookedAt = bookedAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public List<String> getSeats() { return seats; }
    public void setSeats(List<String> seats) { this.seats = seats; }
}