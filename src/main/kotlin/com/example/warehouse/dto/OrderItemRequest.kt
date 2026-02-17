package com.example.warehouse.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class OrderItemRequest(

    @field:NotNull
    val productId: Long,

    @field:Positive
    val quantity: Int
)
