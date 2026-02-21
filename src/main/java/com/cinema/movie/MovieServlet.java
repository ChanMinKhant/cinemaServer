package com.cinema.movie;

import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cinema.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/movies/*")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MovieDao movieDao = new MovieDaoImpl();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Movie> movies = movieDao.findAll();
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Movies retrieved successfully", movies));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiResponse(false, "Failed to retrieve movies: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Jackson will now automatically find the "income" field in JSON
            Movie movie = mapper.readValue(request.getInputStream(), Movie.class);
            movieDao.save(movie);
            sendResponse(response, HttpServletResponse.SC_CREATED, new ApiResponse(true, "Movie added successfully"));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Failed to add movie: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Movie movie = mapper.readValue(request.getInputStream(), Movie.class);
            
            if (movie.getId() == null) {
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Movie ID is required for update"));
                return;
            }
            
            movieDao.update(movie);
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Movie updated successfully"));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Update failed: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Movie ID must be provided in the URL"));
            return;
        }
        
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            movieDao.delete(id);
            sendResponse(response, HttpServletResponse.SC_OK, new ApiResponse(true, "Movie deleted successfully"));
        } catch (NumberFormatException e) {
            sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse(false, "Invalid ID format in URL"));
        } catch (Exception e) {
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiResponse(false, "Delete failed: " + e.getMessage()));
        }
    }

    private void sendResponse(HttpServletResponse response, int status, ApiResponse apiResponse) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}