package com.cinema.showtime;

import com.cinema.common.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDaoImpl implements ShowtimeDao {

    @Override
    public List<Showtime> findAll() {
        List<Showtime> list = new ArrayList<>();
        // Added WHERE is_active = TRUE
        String sql = "SELECT * FROM showtimes WHERE isActive = TRUE"; 
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Showtime(
                    rs.getInt("id"),
                    rs.getInt("movie_id"),
                    rs.getString("room"),
                    rs.getDate("show_date"),
                    rs.getTime("show_time")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching showtimes", e);
        }
        return list;
    }

    @Override
    public void save(Showtime s) {
        String sql = "INSERT INTO showtimes (movie_id, room, show_date, show_time) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getMovieId());
            ps.setString(2, s.getRoom());
            ps.setDate(3, s.getShowDate());
            ps.setTime(4, s.getShowTime());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving showtime: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Showtime s) {
        String sql = "UPDATE showtimes SET movie_id = ?, room = ?, show_date = ?, show_time = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getMovieId());
            ps.setString(2, s.getRoom());
            ps.setDate(3, s.getShowDate());
            ps.setTime(4, s.getShowTime());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating showtime", e);
        }
    }

    @Override
    public void delete(int id) {
        // Changed from DELETE to UPDATE
        String sql = "UPDATE showtimes SET isActive = FALSE WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating showtime", e);
        }
    }
}