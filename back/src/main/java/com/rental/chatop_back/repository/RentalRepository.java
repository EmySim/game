package com.rental.chatop_back.repository;

import com.rental.chatop_back.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Rental entity.
 */
public interface RentalRepository extends JpaRepository<Rental, Long> {
    // No additional methods needed for GET /api/rentals
}
