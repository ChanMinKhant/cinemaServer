package com.cinema.deposit;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/deposits/*")
public class DepositServlet extends HttpServlet {
    private final DepositDaoImpl depositDao = new DepositDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 1. Get User Info from JwtFilter attributes
            String userIdStr = (String) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            int userId = Integer.parseInt(userIdStr);

            List<Deposit> deposits;

            // 2. Conditional Fetching based on Role
            if ("admin".equals(role)) {
                // Admin sees everything (or you could call findAllPending() if you only want a task list)
                deposits = depositDao.findAll(); 
            } else {
                // Regular user only sees their own deposits
                deposits = depositDao.findByUserId(userId);
            }

            // 3. Send Response
            sendResponse(
                response, 
                HttpServletResponse.SC_OK, 
                new ApiResponse(true, "Deposits retrieved successfully", deposits)
            );

        } catch (Exception e) {
            sendResponse(
                response, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                new ApiResponse(false, "Failed to fetch deposits: " + e.getMessage())
            );
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userIdStr = (String) request.getAttribute("userId");
            int userId = Integer.parseInt(userIdStr);

            Deposit deposit = mapper.readValue(request.getInputStream(), Deposit.class);
            deposit.setUserId(userId);

            depositDao.submitDeposit(deposit);
            sendResponse(response, 201, new ApiResponse(true, "Deposit submitted for approval"));
        } catch (Exception e) {
            sendResponse(response, 400, new ApiResponse(false, e.getMessage()));
        }
    }

    // Admin endpoint to approve/reject
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String role = (String) request.getAttribute("role");
            if (!"admin".equals(role)) {
                sendResponse(response, 403, new ApiResponse(false, "Unauthorized"));
                return;
            }

            // Expecting JSON: { "id": 1, "status": "approved", "note": "OK" }
            Deposit updateData = mapper.readValue(request.getInputStream(), Deposit.class);
            depositDao.processDeposit(updateData.getId(), updateData.getStatus(), updateData.getAdminNote());

            sendResponse(response, 200, new ApiResponse(true, "Deposit updated successfully"));
        } catch (Exception e) {
            sendResponse(response, 500, new ApiResponse(false, e.getMessage()));
        }
    }

    private void sendResponse(HttpServletResponse response, int status, ApiResponse api) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(api));
    }
}