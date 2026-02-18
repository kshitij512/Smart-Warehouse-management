package com.example.warehouse.dto

data class UpdateWarehouseRequest(
    val name: String?,
    val location: String?,
    val capacity: Int?,
    val managerId: Long?
)
