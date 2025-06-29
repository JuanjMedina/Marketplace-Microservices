package com.marketplace.userservice.service;

import com.marketplace.userservice.controller.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IKeycloakService {
    List<UserRepresentation> findAllUsers();

    List<UserRepresentation> searchUserByUsername(String username);

    String createUSer(UserDTO userDTO);

    void deleteUser(String userId);

    void updateUser(String userId, UserDTO userDto);
}
