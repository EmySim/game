package com.rental.chatop_back.controller;

import com.rental.chatop_back.dto.UserDTO;
import com.rental.chatop_back.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the details of a specific user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the user details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserDetailsById(id);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }
}
