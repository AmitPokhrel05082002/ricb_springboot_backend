package bt.ricb.ricb_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors->{})
                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                // Stateless session for JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // ================= SWAGGER =================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ================= AUTH APIs =================
                        .requestMatchers(
                                "/claims/auth/login",
                                "/claims/auth/forgot-password",
                                "/claims/auth/reset-password"
                        ).permitAll()

                        // ================= PROTECTED APIs =================
                        .requestMatchers(
                                "/claims/status-counts",
                                "/claims/summaries",
                                "/claims/resubmit",
                                "/claims/complete",
                                "/claims/reject",
                                "/claims/approve",
                                "/claims/rural-claim"
                        ).authenticated()

                        // ================= ADMIN ONLY =================
                        .requestMatchers(
                                "/claims/auth/create-user",
                                "/claims/auth/users",
                                "/claims/auth/user/**",
                                "/claims/auth/update-user/**",
                                "/claims/auth/user-status/**"
                        ).hasRole("IT_OFFICER")

                        // ================= ALL OTHER APIs =================
                        .anyRequest().permitAll()
                )

                // Add JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}