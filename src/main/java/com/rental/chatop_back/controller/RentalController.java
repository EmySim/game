package com.rental.chatop_back.controller;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @GetMapping
    public ResponseEntity<String> getRentals() {
        return ResponseEntity.ok("Liste des locations");
    }
}
