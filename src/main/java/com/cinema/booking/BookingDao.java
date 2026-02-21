package com.cinema.booking;

import java.util.List;

public interface BookingDao {
    void createBooking(Booking booking);
    List<Booking> findByUserId(int userId);
}