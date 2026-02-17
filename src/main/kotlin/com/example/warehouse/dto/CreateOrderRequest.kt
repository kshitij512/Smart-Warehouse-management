package com.example.warehouse.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateOrderRequest(

    @field:NotNull
    val warehouseId: Long,

    @field:Size(min = 1)
    val items: List<OrderItemRequest>
)
