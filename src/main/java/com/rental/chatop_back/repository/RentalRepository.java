package com.rental.chatop_back.repository;

import com.rental.chatop_back.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Rental entity.
 * No additional methods needed for basic CRUD operations.
 */
public interface RentalRepository extends JpaRepository<Rental, Long> {
    // JpaRepository already provides the necessary methods (findById, save, deleteById, etc.)
}
