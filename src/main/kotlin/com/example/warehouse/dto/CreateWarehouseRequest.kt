package com.example.warehouse.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateWarehouseRequest(

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val location: String,

    @field:Positive
    val capacity: Int,

    @field:NotNull
    val managerId: Long
)
