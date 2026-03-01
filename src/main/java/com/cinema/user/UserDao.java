package com.cinema.user;

import java.util.List;

public interface UserDao {
    List<User> findAll();
    User findById(Integer id);
    User findByUsername(String username);
    void save(User user);
    void update(User user);
    void delete(Integer id);
    void updateBalance(int userId, int newBalance);
	User findByEmail(String email);
	User findByPhone(String phone);
}