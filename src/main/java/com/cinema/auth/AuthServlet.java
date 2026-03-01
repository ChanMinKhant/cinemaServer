package com.cinema.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.cinema.common.dto.LoginRequestDTO;
import com.cinema.common.dto.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final AuthService authService = new AuthServiceImpl();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.getWriter().append("Served at: ").append(request.getContextPath());
    	
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if ("/login".equals(pathInfo)) {
                handleLogin(request, response);
            } else if ("/register".equals(pathInfo)) {
                handleRegister(request, response);
            } else if ("/logout".equals(pathInfo)) { // ADD THIS
                handleLogout(response);
            } else {
                sendResponse(response, HttpServletResponse.SC_NOT_FOUND, new ApiResponse(false, "Endpoint not found"));
            }
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, e.getMessage()));
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginRequestDTO dto = mapper.readValue(request.getInputStream(), LoginRequestDTO.class);
        String token = authService.login(dto);

        // JWT in HttpOnly Cookie
        Cookie jwtCookie = new Cookie("auth_token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); 
        response.addCookie(jwtCookie);

        sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Login successful"));
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RegisterRequestDTO dto = mapper.readValue(request.getInputStream(), RegisterRequestDTO.class);
        authService.register(dto);
        
        sendResponse(response, HttpServletResponse.SC_CREATED, new ApiResponse(true, "Registration successful"));
    }

    private void sendResponse(HttpServletResponse response, int status, ApiResponse apiResponse) throws IOException {
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
    
    private void handleLogout(HttpServletResponse response) throws IOException {
        // To logout, we send a cookie with the same name but set its age to 0
        Cookie jwtCookie = new Cookie("auth_token", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // This tells the browser to delete the cookie immediately
        response.addCookie(jwtCookie);

        sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Logout successful"));
    }
}