package com.marketplace.userservice.controller;

import com.marketplace.userservice.controller.dto.ApiResponseDTO;
import com.marketplace.userservice.controller.dto.UserDTO;
import com.marketplace.userservice.service.IKeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/keycloak/users")
@Tag(name = "Keycloak User Management", description = "Operations for managing users in Keycloak realm")
public class KeycloakController {

    private final IKeycloakService keycloakService;

    public KeycloakController(IKeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieve all users from Keycloak realm with their basic information",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Users List Response",
                                            description = "Example response with list of users",
                                            value = """
                                                    {
                                                      "message": "Users retrieved successfully",
                                                      "success": true,
                                                      "data": [
                                                        {
                                                          "id": "4b188d90-816a-4083-b30a-923574fba0b9",
                                                          "username": "john.doe",
                                                          "email": "john@example.com",
                                                          "firstName": "John",
                                                          "lastName": "Doe",
                                                          "enabled": true,
                                                          "emailVerified": true,
                                                          "createdTimestamp": 1750796762104
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - Admin role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Access Denied Response",
                                            description = "Example response when user lacks admin role",
                                            value = """
                                                    {
                                                      "message": "Access denied",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO<List<UserRepresentation>>> getAllUsers() {
        return ResponseEntity.ok(keycloakService.findAllUsers());
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/{username}")
    @Operation(
            summary = "Get user by username",
            description = "Retrieve a specific user from Keycloak by their username",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the user to retrieve",
                    required = true,
                    example = "john.doe"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Found Response",
                                            description = "Example response when user is found",
                                            value = """
                                                    {
                                                      "message": "User found",
                                                      "success": true,
                                                      "data": {
                                                        "id": "4b188d90-816a-4083-b30a-923574fba0b9",
                                                        "username": "john.doe",
                                                        "email": "john@example.com",
                                                        "firstName": "John",
                                                        "lastName": "Doe",
                                                        "enabled": true,
                                                        "emailVerified": true,
                                                        "createdTimestamp": 1750796762104
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Not Found Response",
                                            description = "Example response when user is not found",
                                            value = """
                                                    {
                                                      "message": "User not found with username: john.doe",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - Admin role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Access Denied Response",
                                            description = "Example response when user lacks admin role",
                                            value = """
                                                    {
                                                      "message": "Access denied",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO<UserRepresentation>> getUserByUsername(@PathVariable String username) {
        ApiResponseDTO<UserRepresentation> user = keycloakService.findUserById(username);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Create a new user in Keycloak with the provided information. All fields except roles are required.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User information to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    name = "Create User Request",
                                    description = "Example request body for creating a user",
                                    value = """
                                            {
                                              "username": "john.doe",
                                              "email": "john@example.com",
                                              "firstName": "John",
                                              "lastName": "Doe",
                                              "password": "SecurePass123!",
                                              "roles": ["user"]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Created Response",
                                            description = "Example response when user is created successfully",
                                            value = """
                                                    {
                                                      "message": "User created successfully",
                                                      "success": true,
                                                      "data": "4b188d90-816a-4083-b30a-923574fba0b9"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Validation Error Response",
                                            description = "Example response when validation fails",
                                            value = """
                                                    {
                                                      "message": "Validation failed: email must be valid, username cannot be blank",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Already Exists Response",
                                            description = "Example response when user already exists",
                                            value = """
                                                    {
                                                      "message": "User already exists with username: john.doe",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - Admin role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Access Denied Response",
                                            description = "Example response when user lacks admin role",
                                            value = """
                                                    {
                                                      "message": "Access denied",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO> createUser(
            @Valid @RequestBody UserDTO userDTO) {
        ApiResponseDTO response = keycloakService.createUser(userDTO);
        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @PutMapping("/{username}")
    @Operation(
            summary = "Update a user",
            description = "Update user details in Keycloak. Password field is optional for updates.",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the user to update",
                    required = true,
                    example = "john.doe"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user information",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    name = "Update User Request",
                                    description = "Example request body for updating a user",
                                    value = """
                                            {
                                              "username": "john.doe",
                                              "email": "john.updated@example.com",
                                              "firstName": "John",
                                              "lastName": "Doe Updated",
                                              "password": "NewSecurePass123!",
                                              "roles": ["user", "moderator"]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Updated Response",
                                            description = "Example response when user is updated successfully",
                                            value = """
                                                    {
                                                      "message": "User updated successfully",
                                                      "success": true,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data or validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Validation Error Response",
                                            description = "Example response when validation fails",
                                            value = """
                                                    {
                                                      "message": "Validation failed: email must be valid",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Not Found Response",
                                            description = "Example response when user is not found",
                                            value = """
                                                    {
                                                      "message": "User not found with username: john.doe",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - Admin role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Access Denied Response",
                                            description = "Example response when user lacks admin role",
                                            value = """
                                                    {
                                                      "message": "Access denied",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
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

    @PreAuthorize("hasRole('admin_client_role')")
    @DeleteMapping("/{username}")
    @Operation(
            summary = "Delete a user",
            description = "Delete user from Keycloak by username. This action is irreversible.",
            parameters = @Parameter(
                    name = "username",
                    description = "Username of the user to delete",
                    required = true,
                    example = "john.doe"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "User Not Found Response",
                                            description = "Example response when user is not found",
                                            value = """
                                                    {
                                                      "message": "User not found with username: john.doe",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - Admin role required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Access Denied Response",
                                            description = "Example response when user lacks admin role",
                                            value = """
                                                    {
                                                      "message": "Access denied",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        keycloakService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Return current authenticated user information from Keycloak",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Current user information retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Current User Response",
                                            description = "Example response for getting current authenticated user",
                                            value = """
                                                    {
                                                      "message": "Current user retrieved successfully",
                                                      "success": true,
                                                      "data": {
                                                        "id": "4b188d90-816a-4083-b30a-923574fba0b9",
                                                        "username": "carlos.medina",
                                                        "firstName": "Carlos",
                                                        "lastName": "Medina",
                                                        "email": "carlos@gmail.com",
                                                        "emailVerified": false,
                                                        "attributes": null,
                                                        "userProfileMetadata": null,
                                                        "self": null,
                                                        "origin": null,
                                                        "createdTimestamp": 1750796762104,
                                                        "enabled": true,
                                                        "totp": false,
                                                        "federationLink": null,
                                                        "serviceAccountClientId": null,
                                                        "credentials": null,
                                                        "disableableCredentialTypes": [],
                                                        "requiredActions": [],
                                                        "federatedIdentities": null,
                                                        "realmRoles": null,
                                                        "clientRoles": null,
                                                        "clientConsents": null,
                                                        "notBefore": 0,
                                                        "applicationRoles": null,
                                                        "socialLinks": null,
                                                        "groups": null,
                                                        "access": {
                                                          "manage": true
                                                        }
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Unauthorized Response",
                                            description = "Example response when authentication is required",
                                            value = """
                                                    {
                                                      "message": "Authentication required",
                                                      "success": false,
                                                      "data": null
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponseDTO<UserRepresentation>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getClaim("sub");

        ApiResponseDTO<UserRepresentation> responseDTO = keycloakService.findUserById(keycloakUserId);

        return ResponseEntity.ok(responseDTO);
    }
}
