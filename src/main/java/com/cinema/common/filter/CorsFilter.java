package com.cinema.common.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");

        if (origin != null && isAllowedOrigin(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        response.setHeader(
            "Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS"
        );

        response.setHeader(
            "Access-Control-Allow-Headers",
            "Content-Type, Authorization"
        );

        response.setHeader("Access-Control-Max-Age", "3600");

        // Handle preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isAllowedOrigin(String origin) {
        return
            origin.equals("https://farmguard.site") ||
            origin.equals("http://localhost:5173") ||
            origin.equals("http://localhost:8080") ||
            origin.startsWith("http://192.168.") ||
            origin.startsWith("http://10.") ||
            origin.startsWith("http://172.");
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}