package com.example.warehouse.dto

import com.example.warehouse.model.Role

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    var enabled: Boolean,
    val role: Role
)
