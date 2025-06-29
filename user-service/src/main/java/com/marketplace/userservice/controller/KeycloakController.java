package com.marketplace.userservice.controller;

import com.marketplace.userservice.controller.dto.UserDTO;
import com.marketplace.userservice.service.IKeycloakService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keycloak/user")
@PreAuthorize("hasRole('admin_client_role')")
public class KeycloakController {
    private IKeycloakService keycloakService;

    public KeycloakController(IKeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> findAllUsers() {
        return ResponseEntity.ok(keycloakService.findAllUsers());
    }


    @GetMapping("/search/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(keycloakService.searchUserByUsername(username));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        try {
            String response = keycloakService.createUSer(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Registrar el error detallado
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error detallado: " + e.getMessage());
        }
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateUser(@PathVariable("username") String username, @RequestBody UserDTO userDTO) {
        keycloakService.updateUser(username, userDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser (@PathVariable("username") String username) {
        keycloakService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

}
