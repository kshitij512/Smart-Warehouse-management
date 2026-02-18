package com.example.warehouse.dto


import jakarta.validation.constraints.PositiveOrZero

data class UpdateStockRequest(

    @field:PositiveOrZero
    val quantity: Int
)
