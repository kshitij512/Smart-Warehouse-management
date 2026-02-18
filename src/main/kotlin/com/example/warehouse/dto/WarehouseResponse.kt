package com.example.warehouse.dto

data class WarehouseResponse(
    val id: Long,
    val name: String,
    val location: String,
    val code: String,
    val capacity: Int,
    val managerName: String
)
