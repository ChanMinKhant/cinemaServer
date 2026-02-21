package com.cinema.booking;

import java.sql.Timestamp;
import java.util.List;

public class Booking {
    private Integer id;
    private int userId;
    private int showtime_id;
    private int totalPrice;
    private Timestamp bookedAt;
    private List<Integer> seatIds; // To hold seats being booked

    public Booking() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getShowtimeId() { return showtime_id; }
    public void setShowtimeId(int showtime_id) { this.showtime_id = showtime_id; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
    public Timestamp getBookedAt() { return bookedAt; }
    public void setBookedAt(Timestamp bookedAt) { this.bookedAt = bookedAt; }
    public List<Integer> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Integer> seatIds) { this.seatIds = seatIds; }
}