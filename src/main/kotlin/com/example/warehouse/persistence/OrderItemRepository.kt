package com.example.warehouse.persistence

import com.example.warehouse.model.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long> {

    fun findAllByOrderId(orderId: Long): List<OrderItem>
}
