package com.example.warehouse.dto

data class InventoryResponse(
    val id: Long,
    val warehouseId: Long,
    val productId: Long,
    val productName: String,
    val sku: String,
    val quantity: Int,
    val reorderThreshold: Int
)
