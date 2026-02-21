package com.cinema.showtime;

import java.util.List;

public interface ShowtimeDao {
    List<Showtime> findAll();
    void save(Showtime showtime);
    void update(Showtime showtime);
    void delete(int id);
}