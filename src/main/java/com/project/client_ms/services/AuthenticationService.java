package com.project.client_ms.services;

import com.project.client_ms.dtos.AppUserDTO;
import com.project.client_ms.dtos.AuthenticationRequestDTO;
import com.project.client_ms.dtos.AuthenticationResponseDTO;
import com.project.client_ms.dtos.RegisterRequestDTO;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO);
    AuthenticationResponseDTO register(RegisterRequestDTO registerRequestDTO);
    AuthenticationResponseDTO refreshToken(String authHeader);

}
