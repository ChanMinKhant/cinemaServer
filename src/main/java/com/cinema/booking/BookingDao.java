package com.cinema.booking;

import java.util.List;
import com.cinema.user.User;

public interface BookingDao {
    void createBooking(Booking booking);
    List<Booking> findByUserId(int userId);
    List<BookingView> findDetailedByUserId(int userId);
    List<BookingView> findAllDetailed(); 
    User findUserBySeatAndShowtime(int showtimeId, int seatId);
}