package com.rental.chatop_back.service;

import com.rental.chatop_back.dto.UserDTO;
import com.rental.chatop_back.entity.User;
import com.rental.chatop_back.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserServiceDetail {

    private static final Logger logger = Logger.getLogger(UserServiceDetail.class.getName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceDetail(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserDetailsById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        return new UserDTO(user.getEmail(), user.getName(), user.getPassword());
    }
}