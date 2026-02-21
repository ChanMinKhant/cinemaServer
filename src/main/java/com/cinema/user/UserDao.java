package com.cinema.user;

public interface UserDao {
    User findByUsername(String username);
    void save(User user);
}
