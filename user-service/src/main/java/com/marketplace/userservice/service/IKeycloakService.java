package com.marketplace.userservice.service;

import com.marketplace.userservice.controller.dto.ApiResponseDTO;
import com.marketplace.userservice.controller.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface IKeycloakService {
    ApiResponseDTO<List<UserRepresentation>> findAllUsers();

    ApiResponseDTO<UserRepresentation> searchUserByUsername(String username);

    ApiResponseDTO<String> createUser(UserDTO userDTO);

    ApiResponseDTO<String> deleteUser(String userId);

    ApiResponseDTO<String > updateUser(String userId, UserDTO userDto);

    ApiResponseDTO<UserRepresentation> findUserById(String userId);
}
