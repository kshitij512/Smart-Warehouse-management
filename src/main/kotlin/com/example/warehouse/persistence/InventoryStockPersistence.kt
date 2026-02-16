package com.example.warehouse.persistence

import com.example.warehouse.model.InventoryStock
import com.example.warehouse.model.Product
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryStockPersistence: JpaRepository<InventoryStock, Long> {

    fun findByWarehouseIdAndProductId(warehouseId: Long, productId : Long): InventoryStock?

    fun findAllByWarehouseId(warehouseId : Long): List<InventoryStock>

    fun findAllByProductId(productId: Long): List<InventoryStock>

}