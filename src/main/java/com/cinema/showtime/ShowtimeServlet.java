package com.cinema.showtime;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/showtimes/*")
public class ShowtimeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ShowtimeDao showtimeDao = new ShowtimeDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Showtime> schedules = showtimeDao.findAll();
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Schedules retrieved", schedules));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiResponse(false, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Showtime showtime = mapper.readValue(request.getInputStream(), Showtime.class);
            showtimeDao.save(showtime);
            sendResponse(response, HttpServletResponse.SC_CREATED, new ApiResponse(true, "Schedule added successfully"));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Failed to add schedule: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Showtime showtime = mapper.readValue(request.getInputStream(), Showtime.class);
            if (showtime.getId() == null) {
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Schedule ID required"));
                return;
            }
            showtimeDao.update(showtime);
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Schedule updated successfully"));
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
            showtimeDao.delete(id);
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Schedule deleted successfully"));
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