package com.cinema.deposit;

import com.cinema.common.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepositDaoImpl implements DepositDao { // Added implements

    @Override
    public void submitDeposit(Deposit d) {
        String sql = "INSERT INTO deposits (user_id, amount, payment_method, sender_name, transaction_last_6) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, d.getUserId());
            ps.setLong(2, d.getAmount());
            ps.setString(3, d.getPaymentMethod());
            ps.setString(4, d.getSenderName());
            ps.setString(5, d.getTransactionLast6());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to submit deposit", e);
        }
    }

    @Override
    public void processDeposit(int depositId, String status, String note) {
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false); // Start Transaction

            // 1. Update deposit status
            String updateSql = "UPDATE deposits SET status = ?, admin_note = ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                ps.setString(1, status);
                ps.setString(2, note);
                ps.setInt(3, depositId);
                ps.executeUpdate();
            }

            // 2. If approved, add to user balance
            if ("approved".equals(status)) {
                int userId = -1;
                long amount = 0;

                String infoSql = "SELECT user_id, amount FROM deposits WHERE id = ?";
                try (PreparedStatement ps = c.prepareStatement(infoSql)) {
                    ps.setInt(1, depositId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                        amount = rs.getLong("amount");
                    }
                }

                String balanceSql = "UPDATE users SET balance = balance + ? WHERE id = ?";
                try (PreparedStatement ps = c.prepareStatement(balanceSql)) {
                    ps.setLong(1, amount);
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException("Processing failed", e);
        } finally {
            if (c != null) try { c.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public List<Deposit> findAll() {
        return getDeposits("SELECT * FROM deposits ORDER BY created_at DESC", null);
    }

    @Override
    public List<Deposit> findByUserId(int userId) {
        return getDeposits("SELECT * FROM deposits WHERE user_id = ? ORDER BY created_at DESC", userId);
    }

    @Override
    public List<Deposit> findAllPending() { // Added missing method
        return getDeposits("SELECT * FROM deposits WHERE status = 'pending' ORDER BY created_at ASC", null);
    }

    private List<Deposit> getDeposits(String sql, Integer userIdParam) {
        List<Deposit> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            if (userIdParam != null) {
                ps.setInt(1, userIdParam);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Deposit d = new Deposit();
                d.setId(rs.getInt("id"));
                d.setUserId(rs.getInt("user_id"));
                d.setAmount(rs.getLong("amount"));
                d.setPaymentMethod(rs.getString("payment_method"));
                d.setSenderName(rs.getString("sender_name"));
                d.setTransactionLast6(rs.getString("transaction_last_6"));
                d.setStatus(rs.getString("status"));
                d.setAdminNote(rs.getString("admin_note"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(d);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching deposits", e);
        }
        return list;
    }
}