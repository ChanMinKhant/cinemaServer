package com.cinema.booking;

import com.cinema.common.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDaoImpl implements BookingDao {

    @Override
    public void createBooking(Booking b) {
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false);

            String bookingSql = "INSERT INTO bookings (user_id, showtime_id, total_price) VALUES (?, ?, ?)";
            int bookingId;
            try (PreparedStatement ps = c.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, b.getUserId());
                ps.setInt(2, b.getShowtimeId());
                ps.setInt(3, b.getTotalPrice());
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                } else {
                    throw new SQLException("Booking ID generation failed.");
                }
            }

            String seatSql = "INSERT INTO booking_seats (booking_id, showtime_id, seat_id) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(seatSql)) {
                for (Integer seatId : b.getSeatIds()) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, b.getShowtimeId());
                    ps.setInt(3, seatId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            if (e.getErrorCode() == 1062) {
                throw new RuntimeException("One or more selected seats are already booked for this showtime.");
            }
            throw new RuntimeException("Booking failed: " + e.getMessage(), e);
        } finally {
            if (c != null) {
                try { c.setAutoCommit(true); c.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        List<Booking> list = new ArrayList<>();
        // Select basic booking info
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booked_at DESC";
        
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setShowtimeId(rs.getInt("showtime_id"));
                b.setTotalPrice(rs.getInt("total_price"));
                b.setBookedAt(rs.getTimestamp("booked_at"));
                
                // NEW: Fetch seat IDs associated with this booking
                b.setSeatIds(getSeatsForBooking(c, b.getId()));
                
                list.add(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bookings", e);
        }
        return list;
    }

    // Helper method to fetch seat IDs
    private List<Integer> getSeatsForBooking(Connection c, int bookingId) throws SQLException {
        List<Integer> seatIds = new ArrayList<>();
        String sql = "SELECT seat_id FROM booking_seats WHERE booking_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seatIds.add(rs.getInt("seat_id"));
                }
            }
        }
        return seatIds;
    }
}