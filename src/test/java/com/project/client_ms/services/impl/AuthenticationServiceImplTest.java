package com.project.client_ms.services.impl;

import com.project.client_ms.dtos.AuthenticationResponseDTO;
import com.project.client_ms.dtos.RegisterRequestDTO;
import com.project.client_ms.entities.AppUser;
import com.project.client_ms.repositories.AppUserRepository;
import com.project.client_ms.repositories.TokenRepository;
import com.project.client_ms.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() {
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setUsername("username");
        registerRequestDTO.setPassword("password");

        AppUser appUser = AppUser.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .build();

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("jwtToken");
        when(jwtService.generateRefreshToken(any(AppUser.class))).thenReturn("refreshToken");

        AuthenticationResponseDTO response = authenticationService.register(registerRequestDTO);

        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(appUserRepository, times(1)).save(any(AppUser.class));
        verify(tokenRepository, times(1)).save(any());
    }
}