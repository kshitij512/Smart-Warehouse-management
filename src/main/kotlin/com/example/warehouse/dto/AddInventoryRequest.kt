package com.example.warehouse.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class AddInventoryRequest(

    @field:NotNull
    val warehouseId: Long,

    @field:NotNull
    val productId: Long,

    @field:Min(0)
    val quantity: Int,

    @field:Min(1)
    val reorderThreshold: Int = 10
)
