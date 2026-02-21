package com.cinema.user;

import com.cinema.common.util.DBUtil;
import java.sql.*;

public class UserDaoImpl implements UserDao {

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            User u = new User();
            u.id = rs.getInt("id");
            u.username = rs.getString("username");
            u.password = rs.getString("password");
            u.email = rs.getString("email");
            u.phone = rs.getString("phone");
            u.role = rs.getString("role");
            return u;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save(User u) {
        // FIXED: Added email and phone to the SQL query
        String sql = "INSERT INTO users(username, password, email, phone, role) VALUES(?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.username);
            ps.setString(2, u.password);
            ps.setString(3, u.email);
            ps.setString(4, u.phone);
            ps.setString(5, u.role);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}