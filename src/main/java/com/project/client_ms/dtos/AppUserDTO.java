package com.project.client_ms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AppUserDTO {
    @NotEmpty(message = "Username is required")
    private String username;
    @NotEmpty(message = "Password is required")
    private String password;
}
