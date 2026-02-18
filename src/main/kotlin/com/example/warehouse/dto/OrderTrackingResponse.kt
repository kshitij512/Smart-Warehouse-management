package com.example.warehouse.dto

import java.time.LocalDateTime

data class OrderTrackingResponse(
    val createdAt: LocalDateTime?,
    val confirmedAt: LocalDateTime?,
    val pickingAt: LocalDateTime?,
    val packedAt: LocalDateTime?,
    val shippedAt: LocalDateTime?,
    val deliveredAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?
)