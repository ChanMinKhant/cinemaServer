package com.cinema.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/*")
public class RouterFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no init needed
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        // ✅ Allow API, static files, assets
        if (
            path.startsWith(req.getContextPath() + "/api") ||
            path.contains(".") ||
            path.startsWith(req.getContextPath() + "/WEB-INF")
        ) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ React Router fallback
        request.getRequestDispatcher("/index.html")
               .forward(request, response);
    }

    @Override
    public void destroy() {
        // cleanup if needed
    }
}