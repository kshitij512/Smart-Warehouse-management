package com.example.warehouse.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class UpdateStockRequest(

    @field:NotNull
    val warehouseId: Long,

    @field:NotNull
    val productId: Long,

    @field:PositiveOrZero
    val quantity: Int
)
