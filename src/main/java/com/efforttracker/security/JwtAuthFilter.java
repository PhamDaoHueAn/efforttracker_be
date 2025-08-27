package com.efforttracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private static final String COOKIE_NAME = "access_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // ✅ Bỏ qua endpoint auth
            String path = request.getServletPath();
            if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = null;

            // ✅ Ưu tiên header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }

            // ✅ Fallback cookie nếu không có header
            if (jwt == null) {
                jwt = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                        .filter(c -> COOKIE_NAME.equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }

            if (jwt != null) {
                // Giải mã token
                String userId = jwtService.extractUserId(jwt);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsServiceImpl.loadUserById(userId);

                    // ✅ Kiểm tra token hợp lệ
                    if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities() // ROLE_USER / ROLE_ADMIN
                                );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        System.out.println("JWT Auth successful: " + userDetails.getUsername());
                    } else {
                        System.out.println("JWT invalid for userId: " + userId);
                    }
                }
            } else {
                System.out.println("No JWT found in request");
            }
        } catch (Exception ex) {
            System.out.println("JWT filter error: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
