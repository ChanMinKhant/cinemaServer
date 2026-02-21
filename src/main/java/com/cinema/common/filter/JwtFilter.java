package com.cinema.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.cinema.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

@WebFilter(urlPatterns = {"/api/protected/bookings/*", "/api/admin/*"})
public class JwtFilter implements Filter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        String token = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("auth_token".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                Claims claims = JwtUtil.validate(token);
                
                String userId = claims.getSubject();
                String role = (String) claims.get("role");

                request.setAttribute("userId", userId);
                request.setAttribute("userRole", role);

                // Print to console as requested
                System.out.println("[Middleware] User ID " + userId + " accessed " + request.getRequestURI());

                chain.doFilter(request, response);
            } catch (Exception e) {
                sendError(response, "Unauthorized: Invalid or expired token");
            }
        } else {
            sendError(response, "Unauthorized: No session cookie found");
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ApiResponse apiResponse = new ApiResponse(false, message);
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}