package com.cinema.user;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupResponse(response);
        String pathInfo = request.getPathInfo();

        try {
            // 1. Handle "/api/users/me"
            if (pathInfo != null && pathInfo.equals("/me")) {
                Object userIdAttr = request.getAttribute("userId");
                if (userIdAttr == null) {
                    response.setStatus(401);
                    sendResponse(response, new ApiResponse(false, "Unauthorized", null));
                    return;
                }

                Integer currentUserId = Integer.parseInt(userIdAttr.toString());
                User user = userDao.findById(currentUserId);
                
                if (user != null) {
                    user.password = null; // Ensure password is null before response
                    sendResponse(response, new ApiResponse(true, "Profile retrieved", user));
                } else {
                    response.setStatus(404);
                    sendResponse(response, new ApiResponse(false, "User not found", null));
                }
                return;
            }

            // 2. Standard CRUD: Get All
            if (pathInfo == null || pathInfo.equals("/")) {
                List<User> users = userDao.findAll();
                // Strip passwords from all users in the list
                users.forEach(u -> u.password = null);
                sendResponse(response, new ApiResponse(true, "Users list", users));
            } 
            // 3. Standard CRUD: Get by ID
            else {
                Integer id = Integer.parseInt(pathInfo.substring(1));
                User user = userDao.findById(id);
                if (user != null) {
                    user.password = null; // Strip password
                    sendResponse(response, new ApiResponse(true, "User found", user));
                } else {
                    response.setStatus(404);
                    sendResponse(response, new ApiResponse(false, "User not found", null));
                }
            }
        } catch (Exception e) {
            sendError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupResponse(response);
        try {
            User u = mapper.readValue(request.getReader(), User.class);
            userDao.save(u);
            response.setStatus(201);
            sendResponse(response, new ApiResponse(true, "User created", null));
        } catch (Exception e) {
            sendError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupResponse(response);
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo != null && !pathInfo.equals("/")) {
                Integer id = Integer.parseInt(pathInfo.substring(1));
                User u = mapper.readValue(request.getReader(), User.class);
                u.id = id;
                userDao.update(u);
                sendResponse(response, new ApiResponse(true, "User updated", null));
            } else {
                response.setStatus(400);
                sendResponse(response, new ApiResponse(false, "ID required", null));
            }
        } catch (Exception e) {
            sendError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setupResponse(response);
        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo != null && !pathInfo.equals("/")) {
                Integer id = Integer.parseInt(pathInfo.substring(1));
                userDao.delete(id);
                sendResponse(response, new ApiResponse(true, "User deleted", null));
            } else {
                response.setStatus(400);
                sendResponse(response, new ApiResponse(false, "ID required", null));
            }
        } catch (Exception e) {
            sendError(response, e);
        }
    }

    private void setupResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void sendResponse(HttpServletResponse response, ApiResponse res) throws IOException {
        response.getWriter().write(mapper.writeValueAsString(res));
    }

    private void sendError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(500);
        sendResponse(response, new ApiResponse(false, e.getMessage(), null));
    }
}