package com.FRM.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is required for Login")
        @Email
        String email,
        @NotBlank(message = "Password is required")
        String password
) {
}
