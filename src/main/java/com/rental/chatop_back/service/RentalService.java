package com.rental.chatop_back.service;

import com.rental.chatop_back.entity.Rental;
import com.rental.chatop_back.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }
}
