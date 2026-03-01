package com.cinema.auth;

import com.cinema.common.dto.LoginRequestDTO;
import com.cinema.common.dto.RegisterRequestDTO;
import com.cinema.common.util.JwtUtil;
import com.cinema.common.util.PasswordUtil;
import com.cinema.user.User;
import com.cinema.user.UserDao;
import com.cinema.user.UserDaoImpl;

public class AuthServiceImpl implements AuthService {

    // Ideally, use a Factory or Dependency Injection here
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public void register(RegisterRequestDTO dto) {
        // Validation logic
        validateRegistration(dto);

        if (userDao.findByUsername(dto.username) != null) {
            throw new RuntimeException("Username already taken");
        }
        
        if (userDao.findByEmail(dto.email) != null) {
        	 throw new RuntimeException("Email already taken");
        }
        
        if (userDao.findByPhone(dto.phone) != null) {
       	 throw new RuntimeException("Email already taken");
       }

        User u = new User();
        u.username = dto.username;
        u.password = PasswordUtil.hash(dto.password);
        u.email = dto.email;
        u.phone = dto.phone;
        u.role = "user"; // Default role

        userDao.save(u);
    }

    @Override
    public String login(LoginRequestDTO dto) {
        User u = userDao.findByUsername(dto.username);
        
        if (u == null || !PasswordUtil.verify(dto.password, u.password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return JwtUtil.generateToken(u.id, u.role);
    }

    private void validateRegistration(RegisterRequestDTO dto) {
        if (dto.username == null || dto.username.isBlank()) throw new RuntimeException("Username required");
        if (dto.password == null || dto.password.length() < 6) throw new RuntimeException("Password too short");
        if (dto.email == null || !dto.email.contains("@")) throw new RuntimeException("Invalid email");
    }
}