package com.cinema.booking;

import com.cinema.common.util.DBUtil;
import com.cinema.user.User;
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
                b.setSeatIds(getSeatsForBooking(c, b.getId()));
                list.add(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bookings", e);
        }
        return list;
    }

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
    
    @Override
    public List<BookingView> findDetailedByUserId(int userId) {
        List<BookingView> list = new ArrayList<>();

        String sql =
        	    "SELECT " +
        	    " b.id AS booking_id, " +
        	    " b.total_price, " +
        	    " b.booked_at, " +
        	    " u.username, " +
        	    " m.title AS movie_title, " +
        	    " st.room, " +
        	    " st.show_date, " +
        	    " st.show_time, " +
        	    " GROUP_CONCAT(CONCAT(s.seat_row, s.seat_number) ORDER BY s.seat_row, s.seat_number) AS seats " +
        	    "FROM bookings b " +
        	    "JOIN users u ON b.user_id = u.id " +
        	    "JOIN showtimes st ON b.showtime_id = st.id " +
        	    "JOIN movies m ON st.movie_id = m.id " +
        	    "JOIN booking_seats bs ON bs.booking_id = b.id " +
        	    "JOIN seats s ON bs.seat_id = s.id " +
        	    "WHERE b.user_id = ? " +
        	    "GROUP BY b.id " +
        	    "ORDER BY b.booked_at DESC";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BookingView v = new BookingView();
                v.setId(rs.getInt("booking_id"));
                v.setTotalPrice(rs.getInt("total_price"));
                v.setBookedAt(rs.getTimestamp("booked_at").getTime());
                v.setUsername(rs.getString("username"));
                v.setMovieTitle(rs.getString("movie_title"));
                v.setRoom(rs.getString("room"));
                v.setShowDate(rs.getString("show_date"));
                v.setShowTime(rs.getString("show_time"));

                String seats = rs.getString("seats");
                v.setSeats(seats != null ? List.of(seats.split(",")) : List.of());

                list.add(v);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch detailed bookings", e);
        }

        return list;
    }
    
    @Override
    public List<BookingView> findAllDetailed() {
        List<BookingView> list = new ArrayList<>();

        String sql =
                "SELECT " +
                " b.id AS booking_id, " +
                " b.total_price, " +
                " b.booked_at, " +
                " u.username, " +
                " m.title AS movie_title, " +
                " st.room, " +
                " st.show_date, " +
                " st.show_time, " +
                " GROUP_CONCAT(CONCAT(s.seat_row, s.seat_number) ORDER BY s.seat_row, s.seat_number) AS seats " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id " +
                "JOIN showtimes st ON b.showtime_id = st.id " +
                "JOIN movies m ON st.movie_id = m.id " +
                "JOIN booking_seats bs ON bs.booking_id = b.id " +
                "JOIN seats s ON bs.seat_id = s.id " +
                "GROUP BY b.id " + 
                "ORDER BY b.booked_at DESC";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BookingView v = new BookingView();
                v.setId(rs.getInt("booking_id"));
                v.setTotalPrice(rs.getInt("total_price"));
                v.setBookedAt(rs.getTimestamp("booked_at").getTime());
                v.setUsername(rs.getString("username"));
                v.setMovieTitle(rs.getString("movie_title"));
                v.setRoom(rs.getString("room"));
                v.setShowDate(rs.getString("show_date"));
                v.setShowTime(rs.getString("show_time"));

                String seats = rs.getString("seats");
                v.setSeats(seats != null ? List.of(seats.split(",")) : List.of());

                list.add(v);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all detailed bookings", e);
        }
        return list;
    }

    @Override
    public User findUserBySeatAndShowtime(int showtimeId, int seatId) {
        String sql = "SELECT u.id, u.username, u.email, u.phone, u.balance, u.role, u.created_at " +
                     "FROM users u " +
                     "JOIN bookings b ON u.id = b.user_id " +
                     "JOIN booking_seats bs ON b.id = bs.booking_id " +
                     "WHERE bs.showtime_id = ? AND bs.seat_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setInt(1, showtimeId);
            ps.setInt(2, seatId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.id = rs.getInt("id");
                    u.username = rs.getString("username");
                    u.email = rs.getString("email");
                    u.phone = rs.getString("phone");
                    u.balance = rs.getInt("balance");
                    u.role = rs.getString("role");
                    u.createdAt = rs.getTimestamp("created_at");
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by seat: " + e.getMessage(), e);
        }
        return null;
    }
}