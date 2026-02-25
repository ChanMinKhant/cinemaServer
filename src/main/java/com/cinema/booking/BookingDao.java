package com.cinema.booking;

import java.util.List;

public interface BookingDao {
    void createBooking(Booking booking);

    // OLD (keep)
    List<Booking> findByUserId(int userId);

    // ✅ NEW
    List<BookingView> findDetailedByUserId(int userId);
}