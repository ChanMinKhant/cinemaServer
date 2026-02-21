package com.cinema.seat;

public class Seat {
    private Integer id;
    private String room;
    private String seatRow;
    private int seatNumber;
    private int price;

    public Seat() {}

    public Seat(Integer id, String room, String seatRow, int seatNumber, int price) {
        this.id = id;
        this.room = room;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getSeatRow() { return seatRow; }
    public void setSeatRow(String seatRow) { this.seatRow = seatRow; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}