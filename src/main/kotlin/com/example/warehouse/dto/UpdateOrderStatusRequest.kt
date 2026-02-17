package com.example.warehouse.dto

import com.example.warehouse.model.OrderStatus
import jakarta.validation.constraints.NotNull

data class UpdateOrderStatusRequest(
    @field:NotNull
    val status: OrderStatus
)
