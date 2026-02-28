package com.cinema.booking;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/bookings/*")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookingDao bookingDao = new BookingDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Retrieve userId and role from request attributes (set by JwtFilter)
            String userIdStr = (String) request.getAttribute("userId");
            String role = (String) request.getAttribute("role"); 
            
            int userId = Integer.parseInt(userIdStr);
            List<BookingView> bookings;

            // Logic: Admin gets everything, User gets only their own
            if ("ADMIN".equalsIgnoreCase(role)) {
                bookings = bookingDao.findAllDetailed();
            } else {
                bookings = bookingDao.findDetailedByUserId(userId);
            }

            sendResponse(
                response,
                HttpServletResponse.SC_OK,
                new ApiResponse(true, "Bookings retrieved", bookings)
            );
        } catch (Exception e) {
            sendResponse(
                response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new ApiResponse(false, e.getMessage())
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Get user ID from the JwtFilter attribute to ensure the booking is for the current user
            String userIdStr = (String) request.getAttribute("userId");
            int userId = Integer.parseInt(userIdStr);

            Booking booking = mapper.readValue(request.getInputStream(), Booking.class);
            booking.setUserId(userId); // Override with authenticated ID for security

            bookingDao.createBooking(booking);
            sendResponse(response, HttpServletResponse.SC_CREATED, new ApiResponse(true, "Booking confirmed successfully"));
        } catch (Exception e) {
            // Return specific error message (e.g., "Seats already booked")
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, e.getMessage()));
        }
    }

    private void sendResponse(HttpServletResponse response, int status, ApiResponse apiResponse) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}