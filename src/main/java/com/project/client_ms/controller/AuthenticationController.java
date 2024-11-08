package com.project.client_ms.controller;


import com.project.client_ms.dtos.AuthenticationRequestDTO;
import com.project.client_ms.dtos.AuthenticationResponseDTO;
import com.project.client_ms.dtos.RegisterRequestDTO;

import com.project.client_ms.services.AuthenticationService;
import com.project.client_ms.utils.constants.EndpointConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping(EndpointConstants.ENDPOINT_AUTH)
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        LOGGER.info("Registering user with username: " + registerRequestDTO.getUsername());
        AuthenticationResponseDTO response = authenticationService.register(registerRequestDTO);
        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<List<AppUserDTO>> getAppUsers() {
//        LOGGER.info("Fetching all users");
//        return ResponseEntity.ok(appUserService.getAppUsers());
//    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        LOGGER.info("Authenticating user with username: " + authenticationRequestDTO.getUsername());
        AuthenticationResponseDTO response = authenticationService.authenticate(authenticationRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.refreshToken(authHeader);
        return ResponseEntity.ok(authenticationResponseDTO);
    }


}
