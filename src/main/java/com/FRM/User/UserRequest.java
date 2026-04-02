package com.FRM.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Email is Required")
        @Email(message = "Invalid Email Formate")
        String email,

        @NotBlank(message = "Username is Required")
        String username,

        @NotBlank(message = "Password is required")
        String password) {
}
