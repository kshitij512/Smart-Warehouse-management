package com.example.warehouse.dto



data class OrderItemResponse(
    val productName: String,
    val quantity: Int,
    val price: Double
)
