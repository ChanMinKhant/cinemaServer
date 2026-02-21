package com.cinema.seat;

import java.util.List;

public interface SeatDao {
    List<Seat> findAll();
    List<Seat> findByRoom(String room);
    // New method to fetch booked seat IDs for a specific showtime
    List<Integer> findBookedSeatIdsByShowtime(int showtimeId);
    void save(Seat seat);
    void update(Seat seat);
    void delete(int id);
}