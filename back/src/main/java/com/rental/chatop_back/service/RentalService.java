package com.rental.chatop_back.service;

import com.rental.chatop_back.entity.Rental;
import com.rental.chatop_back.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for handling rental-related operations.
 */
@Service
public class RentalService {

    private static final Logger logger = Logger.getLogger(RentalService.class.getName());
    private final RentalRepository rentalRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * Retrieves all rentals.
     *
     * @return List of rentals.
     */
    public List<Rental> getAllRentals() {
        logger.info("Début de la méthode getAllRentals");

        try {
            List<Rental> rentals = rentalRepository.findAll();
            logger.info("Fin de la méthode getAllRentals");
            return rentals;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des locations", e);
            throw new RuntimeException("Erreur lors de la récupération des locations", e);
        }
    }
}
