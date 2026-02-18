package com.example.warehouse.persistence


import com.example.warehouse.model.OrderTracking
import org.springframework.data.jpa.repository.JpaRepository

interface OrderTrackingPersistence : JpaRepository<OrderTracking, Long> {

    fun findByOrderId(orderId: Long): OrderTracking?
}
