package com.cinema.movie;

import java.util.List;

public interface MovieDao {
    List<Movie> findAll();
    void save(Movie movie);
    void update(Movie movie);
    void delete(int id);
}