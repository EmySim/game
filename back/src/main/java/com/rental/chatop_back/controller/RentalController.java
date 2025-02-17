package com.rental.chatop_back.controller;

import com.rental.chatop_back.entity.Rental;
import com.rental.chatop_back.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling rental-related requests.
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Retrieves all rentals.
     *
     * @return ResponseEntity with the list of rentals.
     */
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        logger.info("Début de la méthode getAllRentals");

        try {
            List<Rental> rentals = rentalService.getAllRentals();
            logger.info("Fin de la méthode getAllRentals");
            return ResponseEntity.ok(rentals);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des locations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Ajoute un message d'erreur ici si tu veux plus de détails
        }
    }
}
