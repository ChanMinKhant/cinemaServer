package com.cinema.seat;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/seats/*")
public class SeatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final SeatDao seatDao = new SeatDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String userIdStr = (String) request.getAttribute("userId");
            int userId = userIdStr != null ? Integer.parseInt(userIdStr) : 0;

            // 1. Handle /api/seats/booked?showtimeId=...
            if (pathInfo != null && pathInfo.equals("/booked")) {
                String showtimeIdStr = request.getParameter("showtimeId");
                if (showtimeIdStr != null && !showtimeIdStr.isBlank()) {
                    int showtimeId = Integer.parseInt(showtimeIdStr);
                    List<Integer> bookedSeatIds = seatDao.findBookedSeatIdsByShowtime(showtimeId);
                    sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Booked seats retrieved", bookedSeatIds));
                    return;
                }
            }

            // 2. Handle /api/seats/my-booking?showtimeId=...
            if (pathInfo != null && pathInfo.equals("/my-booking")) {
                String showtimeIdStr = request.getParameter("showtimeId");
                if (showtimeIdStr != null && !showtimeIdStr.isBlank()) {
                    int showtimeId = Integer.parseInt(showtimeIdStr);
                    // Make sure to implement this method in your DAO!
                    List<Integer> myBookedSeatIds = seatDao.findMyBookedSeatIdsByShowtime(showtimeId, userId);
                    sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "My booked seats retrieved", myBookedSeatIds));
                    return;
                }
            }

            // Default behavior: Fetch seats by room or all seats
            String room = request.getParameter("room");
            List<Seat> seats;
            if (room != null && !room.isBlank()) {
                seats = seatDao.findByRoom(room);
            } else {
                seats = seatDao.findAll();
            }
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Seats retrieved", seats));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiResponse(false, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Seat seat = mapper.readValue(request.getInputStream(), Seat.class);
            seatDao.save(seat);
            sendResponse(response, HttpServletResponse.SC_CREATED, new ApiResponse(true, "Seat created successfully"));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Seat seat = mapper.readValue(request.getInputStream(), Seat.class);
            if (seat.getId() == null) {
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Seat ID required"));
                return;
            }
            seatDao.update(seat);
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Seat updated successfully"));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "ID required in URL"));
            return;
        }
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            seatDao.delete(id);
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Seat deleted successfully"));
        } catch (Exception e) {
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