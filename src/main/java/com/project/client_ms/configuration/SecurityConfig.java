package com.project.client_ms.configuration;

import com.project.client_ms.entities.Token;
import com.project.client_ms.repositories.TokenRepository;
import com.project.client_ms.utils.constants.EndpointConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            EndpointConstants.ENDPOINT_AUTH_PATTERN
    };
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final TokenRepository tokenRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .requestMatchers(GET, EndpointConstants.ENDPOINT_CLIENTS_PATTERN).authenticated()
                                .requestMatchers(POST, EndpointConstants.ENDPOINT_CLIENTS_PATTERN).authenticated()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                        .logout(logout -> logout
                                .logoutUrl(EndpointConstants.ENDPOINT_LOGOUT).addLogoutHandler(
                                        (request, response, authentication) -> {
                                            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
                                            logout(token);
                                        }).logoutSuccessHandler((request, response, authentication) ->
                                            SecurityContextHolder.clearContext()
                                        )
                );
        return http.build();
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowedMethods("GET", "POST")
//                        .allowedHeaders("*")
//                        .allowCredentials(true);
//            }
//        };
//    }
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Collections.singletonList("*"));
    config.setAllowedMethods(Collections.singletonList("*"));
    config.setAllowCredentials(true);
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setExposedHeaders(Arrays.asList("*"));
    config.setMaxAge(3600L); // 1 hora
    source.registerCorsConfiguration("/**", config);
    return source;
}

    private void logout(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String jwtToken = token.substring(7);
        Token foundedToken = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        foundedToken.setExpired(true);
        foundedToken.setRevoked(true);
        tokenRepository.save(foundedToken);
    }
}
