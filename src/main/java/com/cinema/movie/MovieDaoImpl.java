package com.cinema.movie;

import com.cinema.common.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDaoImpl implements MovieDao {

    @Override
    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Added rs.getLong("income") to the constructor
                movies.add(new Movie(
                    rs.getInt("id"), 
                    rs.getString("title"), 
                    rs.getInt("duration"),
                    rs.getLong("income")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching movies", e);
        }
        return movies;
    }

    @Override
    public void save(Movie m) {
        String sql = "INSERT INTO movies (title, duration, income) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setInt(2, m.getDuration());
            ps.setLong(3, m.getIncome()); // Set income parameter
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving movie", e);
        }
    }

    @Override
    public void update(Movie m) {
        String sql = "UPDATE movies SET title = ?, duration = ?, income = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setInt(2, m.getDuration());
            ps.setLong(3, m.getIncome()); // Update income value
            ps.setInt(4, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating movie", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting movie", e);
        }
    }
}