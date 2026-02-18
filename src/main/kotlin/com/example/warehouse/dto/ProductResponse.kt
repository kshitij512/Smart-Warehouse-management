package com.example.warehouse.dto

import java.time.LocalDateTime


data class ProductResponse(
    val id: Long,
    val sku: String,
    var name: String,
    var price: Double,
    val createdAt: LocalDateTime
)
