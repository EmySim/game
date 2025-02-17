package com.rental.chatop_back.controller;

import com.rental.chatop_back.dto.UserDTO;
import com.rental.chatop_back.service.UserServiceDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserServiceDetail userServiceDetail;

    public UserController(UserServiceDetail userServiceDetail) {
        this.userServiceDetail = userServiceDetail;
    }

    /**
     * Retrieves the details of a specific user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the user details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Long id) {
        logger.info("Incoming request to get user details for ID: " + id);
        UserDTO userDTO = userServiceDetail.getUserDetailsById(id);
        if (userDTO == null) {
            logger.warning("User not found for ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Ajout d'une réponse explicite en cas d'absence d'utilisateur
        }
        logger.info("Successfully retrieved user details for ID: " + id);
        return ResponseEntity.ok(userDTO);
    }

}
