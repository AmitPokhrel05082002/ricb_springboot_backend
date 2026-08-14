package bt.ricb.ricb_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // ========================================================
        // NO TOKEN
        // ========================================================

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(7).trim();

            // ====================================================
            // INVALID TOKEN
            // ====================================================

            if (token.isEmpty() || !jwtService.isValid(token)) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.setContentType("application/json");

                response.getWriter().write(
                        "{\"status\":401,\"message\":\"Invalid or expired token\"}"
                );

                return;
            }

            // ====================================================
            // EXTRACT USERNAME / CID
            // ====================================================

            String username = jwtService.extractUsername(token);

            // ====================================================
            // DETERMINE TOKEN TYPE
            // ====================================================

            String tokenType = jwtService.extractTokenType(token);

            List<SimpleGrantedAuthority> authorities;

            // ====================================================
            // MYRICB CUSTOMER TOKEN
            // ====================================================

            if ("CUSTOMER".equalsIgnoreCase(tokenType)) {

                authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_CUSTOMER")
                );

            }

            // ====================================================
            // EXISTING CLAIMS TOKEN
            // ====================================================

            else {

                String role = jwtService.extractRole(token);

                if (role == null || role.isBlank()) {

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );

                    response.setContentType("application/json");

                    response.getWriter().write(
                            "{\"status\":401,\"message\":\"Invalid token role\"}"
                    );

                    return;
                }

                authorities = List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + role
                        )
                );
            }

            // ====================================================
            // CREATE AUTHENTICATION
            // ====================================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");

            response.getWriter().write(
                    "{\"status\":401,\"message\":\"Invalid token\"}"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}