package com.project.client_ms.services.impl;

import com.project.client_ms.dtos.AppUserDTO;
import com.project.client_ms.dtos.AuthenticationRequestDTO;
import com.project.client_ms.dtos.AuthenticationResponseDTO;
import com.project.client_ms.dtos.RegisterRequestDTO;
import com.project.client_ms.entities.AppUser;
import com.project.client_ms.entities.Token;
import com.project.client_ms.repositories.AppUserRepository;
import com.project.client_ms.repositories.TokenRepository;
import com.project.client_ms.security.JwtService;
import com.project.client_ms.services.AppUserService;
import com.project.client_ms.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class.getName());
    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        LOGGER.info("Auth Service Authenticating user with username: " + authenticationRequestDTO.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword())
        );
        AppUser appUser = appUserRepository.findByUsername(authenticationRequestDTO.getUsername())
                .orElseThrow();
        LOGGER.info("user authenticated: " + appUser.getUsername());
        String jwtToken = jwtService.generateToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);
        revoqueAllAppUserTokens(appUser);
        saveAppUserToken(appUser, jwtToken);
        return new AuthenticationResponseDTO(jwtToken, refreshToken);
    }

    @Override
    public AuthenticationResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        LOGGER.info("Registering user with username: " + registerRequestDTO.getUsername());
        AppUser appUser = AppUser.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .build();

        AppUser savedAppUser = appUserRepository.save(appUser);
        String jwtToken = jwtService.generateToken(savedAppUser);
        String refreshToken = jwtService.generateRefreshToken(savedAppUser);
        saveAppUserToken(savedAppUser, jwtToken);
        return new AuthenticationResponseDTO(jwtToken, refreshToken);
    }

    private void saveAppUserToken(AppUser appUser, String jwtToken) {
        Token token = Token.builder()
                .token(jwtToken)
                .appUser(appUser)
                .build();
        tokenRepository.save(token);
    }

    private void revoqueAllAppUserTokens(AppUser appUser) {
        List<Token> validTokens = tokenRepository.findAllValidTokenByAppUserId(appUser.getId());
        if (!validTokens.isEmpty()) {
            for (Token token : validTokens) {
                token.setExpired(true);
                token.setRevoked(true);
            }
            tokenRepository.saveAll(validTokens);
        }
    }

    public AuthenticationResponseDTO refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header during token refresh");
        }

        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow();

        if (!jwtService.isTokenValid(refreshToken, appUser)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String accessToken = jwtService.generateToken(appUser);
        revoqueAllAppUserTokens(appUser);
        saveAppUserToken(appUser, accessToken);
        return new AuthenticationResponseDTO(accessToken, refreshToken);

    }
}
