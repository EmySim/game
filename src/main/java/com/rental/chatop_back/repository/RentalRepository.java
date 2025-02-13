package com.rental.chatop_back.repository;

import com.rental.chatop_back.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    // Pas besoin de méthode supplémentaire pour GET /api/rentals
}
