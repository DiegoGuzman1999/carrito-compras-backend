package com.shoppingcart.backend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppingcart.backend.service.UserService;
import com.shoppingcart.backend.dto.user.CreateUserRequest;
import com.shoppingcart.backend.dto.user.UpdateUserRequest;
import com.shoppingcart.backend.dto.user.UserResponse;
import com.shoppingcart.backend.entity.User;
import com.shoppingcart.backend.exception.BusinessException;
import com.shoppingcart.backend.exception.ResourceNotFoundException;
import com.shoppingcart.backend.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessException("A user with email " + normalizedEmail + " already exists");
        }

        User user = new User();
        user.setFirstName(normalizeText(request.firstName()));
        user.setLastName(normalizeText(request.lastName()));
        user.setEmail(normalizedEmail);
        user.setActive(Boolean.TRUE);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));

        if (request.firstName() != null) {
            user.setFirstName(normalizeText(request.firstName()));
        }

        if (request.lastName() != null) {
            user.setLastName(normalizeText(request.lastName()));
        }

        if (request.email() != null) {

            String normalizedEmail = normalizeEmail(request.email());

            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)
                    && !normalizedEmail.equalsIgnoreCase(user.getEmail())) {

                throw new BusinessException("A user with email " + normalizedEmail + " already exists");
            }

            user.setEmail(normalizedEmail);
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));

        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }
}