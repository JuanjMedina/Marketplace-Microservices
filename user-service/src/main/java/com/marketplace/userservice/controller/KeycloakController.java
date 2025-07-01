package com.marketplace.userservice.controller;

import com.marketplace.userservice.controller.dto.ApiResponseDTO;
import com.marketplace.userservice.controller.dto.UserDTO;
import com.marketplace.userservice.service.IKeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/users")
@Tag(name = "Keycloak User Management", description = "Operations for managing users in Keycloak")
@PreAuthorize("hasRole('admin_client_role')")
public class KeycloakController {

    private final IKeycloakService keycloakService;

    public KeycloakController(IKeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieve all users from Keycloak",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "List of users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserRepresentation.class))
                    )
            )
    )
    public ResponseEntity<ApiResponseDTO<List<UserRepresentation>>> getAllUsers() {
        return ResponseEntity.ok(keycloakService.findAllUsers());
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get user by username",
            description = "Retrieve a specific user from Keycloak by username",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserRepresentation.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"User not found\", \"message\": \"No user was found with the provided username\"}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO<UserRepresentation>> getUserByUsername(@PathVariable String username) {
        ApiResponseDTO<UserRepresentation> user = keycloakService.findUserById(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Create a user in Keycloak",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"Validation error\", \"message\": \"Invalid user data\"}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO> createUser(
            @Valid @RequestBody UserDTO userDTO) {
        ApiResponseDTO response = keycloakService.createUser(userDTO);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{username}")
    @Operation(
            summary = "Update a user",
            description = "Update user details in Keycloak",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"User not found\", \"message\": \"No user was found with the provided username\"}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UserDTO userDTO) {
        ApiResponseDTO response = keycloakService.updateUser(username, userDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}")
    @Operation(
            summary = "Delete a user",
            description = "Delete user from Keycloak by username",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"User not found\", \"message\": \"No user was found with the provided username\"}")
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        keycloakService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
