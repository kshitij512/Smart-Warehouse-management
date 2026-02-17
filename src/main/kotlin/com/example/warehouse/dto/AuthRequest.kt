package com.example.warehouse.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "Login request payload")
data class AuthRequest(

    @field:Email
    @field:NotBlank
    @Schema(example = "admin@warehouse.com")
    val email: String,

    @field:NotBlank
    @Schema(example = "admin123")
    val password: String
)