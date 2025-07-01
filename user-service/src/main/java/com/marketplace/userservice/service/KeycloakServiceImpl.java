package com.marketplace.userservice.service;

import com.marketplace.userservice.controller.dto.ApiResponseDTO;
import com.marketplace.userservice.controller.dto.UserDTO;
import com.marketplace.userservice.exception.UserAlreadyExistsException;
import com.marketplace.userservice.utils.KeycloakProvider;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class KeycloakServiceImpl implements IKeycloakService {

    private UserRepresentation toUserRepresentation(UserDTO dto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.username());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setEnabled(true);
        user.setEmailVerified(true);
        return user;
    }

    private String getUserIdFromPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private void assignPasswordAndRoles(String userId, @NonNull UserDTO userDTO) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(OAuth2Constants.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(userDTO.password());

        UserResource userResource = KeycloakProvider.getUserResource().get(userId);
        userResource.resetPassword(credential);

        RealmResource realmResource = KeycloakProvider.getRealmResource();
        List<RoleRepresentation> roleRepresentations = (userDTO.roles() == null || userDTO.roles().isEmpty())
                ? List.of(realmResource.roles().get("BUYER").toRepresentation())
                : userDTO.roles().stream()
                .map(role -> realmResource.roles().get(role).toRepresentation())
                .toList();

        userResource.roles().realmLevel().add(roleRepresentations);
    }

    @Override
    public ApiResponseDTO<UserRepresentation> findUserById(String userId) {
        UserResource userResource = KeycloakProvider.getUserResource().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        if (userRepresentation == null) {
            throw new UserAlreadyExistsException("User with ID '" + userId + "' does not exist.");
        }
        return new ApiResponseDTO<>("User found successfully", true, userRepresentation);
    }

    @Override
    public ApiResponseDTO<List<UserRepresentation>> findAllUsers() {
        List<UserRepresentation> userRepresentationList = KeycloakProvider.getRealmResource().users().list();

        if (userRepresentationList == null || userRepresentationList.isEmpty()) {
            throw new UserAlreadyExistsException("No users found in the realm.");
        }

        return new ApiResponseDTO<>("Users retrieved successfully", true, userRepresentationList);
    }

    @Override
    public ApiResponseDTO<UserRepresentation> searchUserByUsername(String username) {
        List<UserRepresentation> users = KeycloakProvider.getRealmResource()
                .users().searchByUsername(username, true);

        if (users == null || users.isEmpty()) {
            throw new UserAlreadyExistsException("No user found with username: " + username);
        }

        UserRepresentation userRepresentation = users.get(0);
        return new ApiResponseDTO<>("User found successfully", true, userRepresentation);
    }

    @Override
    public ApiResponseDTO createUser(@NonNull UserDTO userDTO) {
        UsersResource usersResource = KeycloakProvider.getUserResource();
        UserRepresentation userRepresentation = toUserRepresentation(userDTO);

        Response response = usersResource.create(userRepresentation);
        int status = response.getStatus();

        if (status == 201) {
            String userId = getUserIdFromPath(response.getLocation().getPath());
            assignPasswordAndRoles(userId, userDTO);
            return new ApiResponseDTO<String>("User created successfully", true, userId);
        } else if (status == 409) {
            throw new UserAlreadyExistsException("User with username '" + userDTO.username() + "' already exists.");
        } else {
            throw new RuntimeException("Failed to create the user. Status code: " + status);
        }
    }

    @Override
    public ApiResponseDTO<String> deleteUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        findUserById(userId); // Lanza excepción si no existe
        KeycloakProvider.getUserResource().get(userId).remove();
        return new ApiResponseDTO("User deleted successfully", true, userId);
    }

    @Override
    public ApiResponseDTO<String> updateUser(String userId, @NonNull UserDTO userDto) {
        UserResource userResource = KeycloakProvider.getUserResource().get(userId);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDto.firstName());
        userRepresentation.setLastName(userDto.lastName());
        userRepresentation.setUsername(userDto.username());
        userRepresentation.setEmail(userDto.email());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        userResource.update(userRepresentation);

        if (userDto.password() != null && !userDto.password().isEmpty()) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(OAuth2Constants.PASSWORD);
            credential.setTemporary(false);
            credential.setValue(userDto.password());
            userResource.resetPassword(credential);
        }

        return new ApiResponseDTO("User updated successfully", true, userId);
    }
}
