package com.example.warehouse.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Authentication response")
data class AuthResponse(

    @Schema(description = "JWT access token")
    val accessToken: String,

    @Schema(example = "ADMIN")
    val role: String
)