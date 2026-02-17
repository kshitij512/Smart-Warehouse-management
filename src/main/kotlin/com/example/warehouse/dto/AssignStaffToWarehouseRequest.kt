package com.example.warehouse.dto

import org.jetbrains.annotations.NotNull

data class AssignStaffToWarehouseRequest(
    @field:NotNull
    val staffId: Long,

    @field:NotNull
    val warehouseId: Long
)
