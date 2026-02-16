package com.example.warehouse.persistence

import com.example.warehouse.model.Order
import com.example.warehouse.model.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderPersistence: JpaRepository<Order, Long> {

    fun findAllByStatus(status: OrderStatus): List<Order>

    fun findAllByWarehouseId(warehouseId: Long): List<Order>

    fun findAllByAssignedStaffId(staffId: Long): List<Order>

}