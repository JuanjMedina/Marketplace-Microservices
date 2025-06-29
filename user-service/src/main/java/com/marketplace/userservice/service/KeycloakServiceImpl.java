package com.marketplace.userservice.service;

import com.marketplace.userservice.controller.dto.UserDTO;
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

import java.util.Collections;
import java.util.List;

/**
 * Service implementation for interacting with Keycloak.
 * Provides methods for managing users in the Keycloak realm.
 */
@Service
public class KeycloakServiceImpl implements IKeycloakService {

    /**
     * Retrieves all users from the Keycloak realm.
     *
     * @return List of UserRepresentation objects representing all users.
     */
    @Override
    public List<UserRepresentation> findAllUsers() {
        return KeycloakProvider.getRealmResource().users().list();
    }

    /**
     * Searches for users in the Keycloak realm by username.
     *
     * @param username The username to search for.
     * @return List of UserRepresentation objects matching the username.
     */
    @Override
    public List<UserRepresentation> searchUserByUsername(String username) {
        return KeycloakProvider.getRealmResource().users().searchByUsername(username, true);
    }

    /**
     * Creates a new user in the Keycloak realm.
     *
     * @param userDTO The UserDTO object containing user details.
     * @return A message indicating the result of the operation.
     */
    @Override
    public String createUSer(@NonNull UserDTO userDTO) {
        int status = 0;
        UsersResource userResource = KeycloakProvider.getUserResource();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());
        userRepresentation.setUsername(userDTO.username());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        // Log para depuración
        System.out.println("Intentando crear usuario: " + userDTO.username());

        Response response = userResource.create(userRepresentation);
        status = response.getStatus();

        // Log de respuesta para depuración
        System.out.println("Respuesta del servidor: " + status + " - " + response.getStatusInfo().getReasonPhrase());

        if (status == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(OAuth2Constants.PASSWORD);
            credential.setTemporary(false);
            credential.setValue(userDTO.password());

            userResource.get(userId).resetPassword(credential);

            RealmResource realmResource = KeycloakProvider.getRealmResource();
            List<RoleRepresentation> roleRepresentations = null;
            if (userDTO.roles() == null || userDTO.roles().isEmpty()) {
                roleRepresentations = List.of(
                        realmResource.roles().get("BUYER").toRepresentation()
                );
            } else {
                roleRepresentations = userDTO.roles().stream().map(role -> realmResource.roles().get(role).toRepresentation())
                        .toList();
            }

            realmResource.users().get(userId).roles().realmLevel().add(roleRepresentations);

            return "User create sucessfully with iD : " + userId;
        } else if (status == 409) {
            return "User already exists with username: " + userDTO.username();
        } else {
            return "Error creating user: " + response.getStatusInfo().getReasonPhrase() + " (Status: " + status + ")";
        }
    }

    /**
     * Deletes a user from the Keycloak realm.
     *
     * @param userId The ID of the user to delete.
     */
    @Override
    public void deleteUser(String userId) {
        KeycloakProvider.getUserResource().get(userId).remove();
    }

    /**
     * Updates an existing user in the Keycloak realm.
     *
     * @param userId  The ID of the user to update.
     * @param userDto The UserDTO object containing updated user details.
     */
    @Override
    public void updateUser(String userId, @NonNull UserDTO userDto) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(OAuth2Constants.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(userDto.password());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userDto.firstName());
        userRepresentation.setLastName(userDto.lastName());
        userRepresentation.setUsername(userDto.username());
        userRepresentation.setEmail(userDto.email());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credential));

        UserResource userResource = KeycloakProvider.getUserResource().get(userId);
        userResource.update(userRepresentation);
    }
}