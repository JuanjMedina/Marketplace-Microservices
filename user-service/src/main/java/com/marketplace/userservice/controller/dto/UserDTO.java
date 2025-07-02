package com.marketplace.userservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "User data transfer object for creating and updating users")
public record UserDTO(

        @Schema(description = "Unique username for the user", example = "john.doe", required = true)
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "Valid email address of the user", example = "john@example.com", required = true)
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "First name of the user", example = "John", required = true)
        @NotBlank(message = "First name cannot be blank")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Schema(description = "Last name of the user", example = "Doe", required = true)
        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Schema(description = "Password for the user account (minimum 8 characters)", example = "SecurePass123!", required = true)
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @Schema(description = "Set of roles assigned to the user", example = "[\"user\", \"BUTCHER\"]", required = false)
        Set<String> roles
) {
}