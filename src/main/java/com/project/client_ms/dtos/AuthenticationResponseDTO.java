package com.project.client_ms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {

    private String accessToken;
    private String refreshToken;
}
