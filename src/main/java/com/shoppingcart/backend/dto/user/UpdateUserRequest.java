package com.shoppingcart.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @Size(max = 100, message = "firstName must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "lastName must not exceed 100 characters")
        String lastName,

        @Email(message = "email must be a valid email address")
        @Size(max = 150, message = "email must not exceed 150 characters")
        String email
) {
}