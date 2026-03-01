package com.cinema.seat;

import com.cinema.common.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDaoImpl implements SeatDao {

    @Override
    public List<Seat> findAll() {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                seats.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all seats", e);
        }
        return seats;
    }

    @Override
    public List<Seat> findByRoom(String room) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE room = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, room);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching seats by room", e);
        }
        return seats;
    }

    @Override
    public List<Integer> findBookedSeatIdsByShowtime(int showtimeId) {
        List<Integer> bookedIds = new ArrayList<>();
        String sql = "SELECT seat_id FROM booking_seats WHERE showtime_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookedIds.add(rs.getInt("seat_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching booked seats", e);
        }
        return bookedIds;
    }

    @Override
    public List<Integer> findMyBookedSeatIdsByShowtime(int showtimeId, int userId) {
        List<Integer> myBookedSeatIds = new ArrayList<>();
        // Updated to use 'booking_seats' joined with 'bookings' to filter by user
        String sql = "SELECT bs.seat_id " +
                     "FROM booking_seats bs " +
                     "JOIN bookings b ON bs.booking_id = b.id " +
                     "WHERE bs.showtime_id = ? AND b.user_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setInt(1, showtimeId);
            ps.setInt(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    myBookedSeatIds.add(rs.getInt("seat_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user's booked seats", e);
        }
        return myBookedSeatIds;
    }

    @Override
    public void save(Seat s) {
        String sql = "INSERT INTO seats (room, seat_row, seat_number, price) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getRoom());
            ps.setString(2, s.getSeatRow());
            ps.setInt(3, s.getSeatNumber());
            ps.setInt(4, s.getPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving seat: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Seat s) {
        String sql = "UPDATE seats SET room = ?, seat_row = ?, seat_number = ?, price = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getRoom());
            ps.setString(2, s.getSeatRow());
            ps.setInt(3, s.getSeatNumber());
            ps.setInt(4, s.getPrice());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating seat", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM seats WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting seat", e);
        }
    }

    private Seat mapRow(ResultSet rs) throws SQLException {
        return new Seat(
            rs.getInt("id"),
            rs.getString("room"),
            rs.getString("seat_row"),
            rs.getInt("seat_number"),
            rs.getInt("price")
        );
    }
}