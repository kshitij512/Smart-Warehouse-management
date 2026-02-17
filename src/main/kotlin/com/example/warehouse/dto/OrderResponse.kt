package com.example.warehouse.dto

import com.example.warehouse.model.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val status: OrderStatus,
    val totalAmount: Double,
    val warehouseName: String,
    val assignedStaffName: String?,
    val createdAt: LocalDateTime,
    val items: List<OrderItemResponse>
)
