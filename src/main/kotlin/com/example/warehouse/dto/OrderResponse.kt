package com.example.warehouse.dto

import com.example.warehouse.model.OrderItem
import com.example.warehouse.model.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val status: OrderStatus,
    val customerName: String,
    val totalAmount: Double,
    val warehouseId: Long,
    val assignedStaffName: String?,
    val createdAt: LocalDateTime,
    val items: MutableList<OrderItemResponse>

)
