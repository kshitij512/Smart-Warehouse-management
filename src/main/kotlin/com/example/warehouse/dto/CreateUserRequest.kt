package com.example.warehouse.dto

import com.example.warehouse.model.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateUserRequest(

    @field:NotBlank
    val name: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String,

    @field:NotNull
    val role: Role
)
