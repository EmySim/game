package com.rental.chatop_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @GetMapping
    public ResponseEntity<String> getRentals() {
        return ResponseEntity.ok("{\n" +
                "  \"message\": \"Rental created !\"\n" +
                "}");
    }
}
