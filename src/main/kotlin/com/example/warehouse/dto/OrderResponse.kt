package com.example.warehouse.dto

import com.example.warehouse.model.OrderItem
import com.example.warehouse.model.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val warehouseId: Long,
    val customerName: String,
    val totalAmount: Double,
    val status: OrderStatus,
    val assignedStaffName: String?,
    val createdAt: LocalDateTime,
    val items: List<OrderItemResponse>, // ✅ FIXED
    val allowedTransitions: List<OrderStatus>
)