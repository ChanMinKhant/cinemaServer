package com.cinema.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/protected/profile")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve info provided by the JwtFilter
        String userId = (String) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        // Print to console
        System.out.println("Processing profile data for User ID: " + userId);

        // Prepare data map for response
        Map<String, String> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("role", role);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        ApiResponse apiResponse = new ApiResponse(true, "User profile retrieved", userData);
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}