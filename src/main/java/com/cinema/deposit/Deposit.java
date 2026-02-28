package com.cinema.deposit;

import java.sql.Timestamp;

public class Deposit {
    private Integer id;
    private int userId;
    private long amount;
    private String paymentMethod;
    private String senderName;
    private String transactionLast6;
    private String status; // pending, approved, rejected
    private String adminNote;
    private Timestamp createdAt;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getTransactionLast6() { return transactionLast6; }
    public void setTransactionLast6(String transactionLast6) { this.transactionLast6 = transactionLast6; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}