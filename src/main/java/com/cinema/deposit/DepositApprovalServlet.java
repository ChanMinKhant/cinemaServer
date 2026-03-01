package com.cinema.deposit;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@WebServlet("/api/admin/deposits/*")
public class DepositApprovalServlet extends HttpServlet {
    private final DepositDaoImpl depositDao = new DepositDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    // Admin endpoint to approve/reject
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Verify admin role (assuming this is set upstream by an Auth Filter)
        	
            String role = (String) request.getAttribute("userRole");
            System.out.println(role);
            if (!"admin".equals(role)) {
                sendResponse(response, 403, new ApiResponse(false, "Unauthorized: Admin privileges required"));
                return;
            }

            // Parse incoming JSON: { "id": 1, "status": "approved", "adminNote": "All good" }
            Deposit updateData = mapper.readValue(request.getInputStream(), Deposit.class);
            
            // Process the transaction
            depositDao.processDeposit(updateData.getId(), updateData.getStatus(), updateData.getAdminNote());

            sendResponse(response, 200, new ApiResponse(true, "Deposit status updated to: " + updateData.getStatus()));
        } catch (Exception e) {
            sendResponse(response, 500, new ApiResponse(false, "Error processing deposit: " + e.getMessage()));
        }
    }

    private void sendResponse(HttpServletResponse response, int status, ApiResponse api) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(api));
    }
}