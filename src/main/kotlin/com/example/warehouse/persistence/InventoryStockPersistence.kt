package com.example.warehouse.persistence

import com.example.warehouse.model.InventoryStock
import com.example.warehouse.model.Product
import com.example.warehouse.model.Warehouse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface InventoryStockPersistence: JpaRepository<InventoryStock, Long> {

    fun findByWarehouseIdAndProductId(warehouseId: Long, productId : Long): InventoryStock?

    fun findAllByWarehouseId(warehouseId : Long): List<InventoryStock>

    fun findAllByProductId(productId: Long): List<InventoryStock>

    fun findByWarehouseId(warehouseId: Long): List<InventoryStock>


}