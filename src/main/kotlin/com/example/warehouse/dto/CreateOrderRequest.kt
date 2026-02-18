package com.example.warehouse.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateOrderRequest(

    @field:NotNull
    val warehouseId: Long,

    @field:NotBlank
    val customerName: String,

    @field:NotBlank
    val customerEmail: String,

    @field:NotBlank
    val customerAddress: String,

    @field:NotBlank
    val customerPhone: String,

    val items: List<OrderItemRequest>
)
