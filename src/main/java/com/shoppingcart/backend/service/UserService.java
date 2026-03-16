package com.shoppingcart.backend.service;

import java.util.List;

import com.shoppingcart.backend.dto.user.CreateUserRequest;
import com.shoppingcart.backend.dto.user.UpdateUserRequest;
import com.shoppingcart.backend.dto.user.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}
