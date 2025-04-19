package com.care4elders.userservice.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.setHeader("X-Content-Type-Options", "nosniff");
        httpResp.setHeader("X-XSS-Protection", "1; mode=block");
        httpResp.setHeader("X-Frame-Options", "DENY");

        chain.doFilter(request, response);
    }
}
