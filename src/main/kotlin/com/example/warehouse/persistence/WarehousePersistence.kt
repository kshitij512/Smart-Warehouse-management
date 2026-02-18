package com.example.warehouse.persistence

import com.example.warehouse.model.Warehouse
import org.springframework.data.jpa.repository.JpaRepository

interface WarehousePersistence: JpaRepository<Warehouse, Long> {

    fun existsByCode(name :String): Boolean

    fun findByLocation(location: String): List<Warehouse>

    fun findByManagerId(managerId: Long): List<Warehouse>
}