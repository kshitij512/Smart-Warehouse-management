package com.example.warehouse.dto



data class ProductResponse(
    val id: Long,
    val sku: String,
    val name: String,
    val price: Double,
    val reorderThreshold: Int
)
