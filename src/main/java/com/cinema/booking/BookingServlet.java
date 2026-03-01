package com.cinema.booking;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.cinema.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/bookings/*")
public class BookingServlet extends HttpServlet {
    private final BookingDao bookingDao = new BookingDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String role = (String) request.getAttribute("userRole");
            String userIdStr = (String) request.getAttribute("userId");

            // Handle Admin User Lookup for a specific seat
            if (pathInfo != null && pathInfo.equals("/seat-user")) {
                if (!"admin".equalsIgnoreCase(role)) {
                    sendResponse(response, 403, new ApiResponse(false, "Admin access required"));
                    return;
                }

                String showtimeIdParam = request.getParameter("showtimeId");
                String seatIdParam = request.getParameter("seatId");

                if (showtimeIdParam == null || seatIdParam == null) {
                    sendResponse(response, 400, new ApiResponse(false, "Missing showtimeId or seatId"));
                    return;
                }

                User user = bookingDao.findUserBySeatAndShowtime(
                    Integer.parseInt(showtimeIdParam), 
                    Integer.parseInt(seatIdParam)
                );

                if (user != null) {
                    sendResponse(response, 200, new ApiResponse(true, "User found", user));
                } else {
                    sendResponse(response, 404, new ApiResponse(false, "No booking found for this seat"));
                }
                return;
            }

            // Standard Booking Retrieval
            if (userIdStr == null) {
                sendResponse(response, 401, new ApiResponse(false, "Unauthorized"));
                return;
            }

            int userId = Integer.parseInt(userIdStr);
            List<BookingView> result;

            if ("admin".equalsIgnoreCase(role)) {
                result = bookingDao.findAllDetailed();
            } else {
                result = bookingDao.findDetailedByUserId(userId);
            }
            sendResponse(response, 200, new ApiResponse(true, "Success", result));

        } catch (Exception e) {
            sendResponse(response, 500, new ApiResponse(false, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userIdStr = (String) request.getAttribute("userId");
            Booking booking = mapper.readValue(request.getInputStream(), Booking.class);
            booking.setUserId(Integer.parseInt(userIdStr));
            bookingDao.createBooking(booking);
            sendResponse(response, 201, new ApiResponse(true, "Booking successful"));
        } catch (Exception e) {
            sendResponse(response, 400, new ApiResponse(false, e.getMessage()));
        }
    }

    private void sendResponse(HttpServletResponse response, int status, ApiResponse apiResponse) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}