package com.project.client_ms.configuration;

import com.project.client_ms.dtos.AuthenticationResponseDTO;
import com.project.client_ms.entities.AppUser;
import com.project.client_ms.entities.Token;
import com.project.client_ms.exceptions.ExpiredTokenException;
import com.project.client_ms.exceptions.InvalidTokenException;
import com.project.client_ms.repositories.AppUserRepository;
import com.project.client_ms.repositories.TokenRepository;
import com.project.client_ms.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final AppUserRepository appUserRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (ExpiredTokenException e) {
            logger.warn("Token expired: " + e.getMessage());
            handleException(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (InvalidTokenException e) {
            logger.warn("Invalid token: " + e.getMessage());
            handleException(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        Token token = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if (token == null || token.isExpired() || token.isRevoked()) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if(appUser.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isTokenValid = jwtService.isTokenValid(jwtToken, appUser.get());
        if (!isTokenValid) {
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);

    }

    private void handleException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
