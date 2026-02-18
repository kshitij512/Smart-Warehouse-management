package com.example.warehouse.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateProductRequest(

    @field:NotBlank
    val sku: String,

    @field:NotBlank
    val name: String,

    @field:Positive
    val price: Double,

)
