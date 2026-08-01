package com.intellilearn.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.intellilearn.security.filter.JwtAuthFilter;
import com.intellilearn.security.handler.CustomAccessDeniedHandler;
import com.intellilearn.security.handler.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                           CustomAuthenticationEntryPoint authenticationEntryPoint,
                           CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                    
                    .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                    
                    .requestMatchers("/", "/login", "/register", "/home", "/teacher/dashboard", "/student/dashboard").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()

                   
                    .requestMatchers(HttpMethod.POST, "/subjects/**").hasRole("TEACHER")
                    .requestMatchers(HttpMethod.PUT, "/subjects/**").hasRole("TEACHER")
                    .requestMatchers(HttpMethod.DELETE, "/subjects/**").hasRole("TEACHER")
                    .requestMatchers(HttpMethod.GET, "/subjects/**").authenticated()

                    
                    .requestMatchers(HttpMethod.POST, "/notes/upload/**").hasRole("TEACHER")
                    .requestMatchers(HttpMethod.DELETE, "/notes/**").hasRole("TEACHER")
                    .requestMatchers(HttpMethod.GET, "/notes/**").authenticated()

                    
                    .requestMatchers(HttpMethod.POST, "/quiz/generate/**").hasRole("STUDENT")
                    .requestMatchers(HttpMethod.GET, "/quiz/**").authenticated()

                    
                    .requestMatchers("/quiz-attempt/**").hasRole("STUDENT")

                    
                    .requestMatchers("/dashboard/**").authenticated()

                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}