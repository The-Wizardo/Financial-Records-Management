package com.FRM.Auth;

import com.FRM.User.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UserRequest(
        @NotBlank(message = "Email is Required")
        @Email(message = "Invalid Email Formate")
        String email,

        @NotBlank(message = "Username is Required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        Set<Roles> roles
        ) {
}
