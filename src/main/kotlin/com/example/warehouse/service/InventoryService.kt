package com.example.warehouse.service


import com.example.warehouse.dto.AddInventoryRequest
import com.example.warehouse.dto.InventoryResponse
import com.example.warehouse.dto.UpdateStockRequest
import com.example.warehouse.exception.BadRequestException
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.NotFoundException
import com.example.warehouse.model.InventoryStock
import com.example.warehouse.model.OrderItem
import com.example.warehouse.persistence.InventoryStockPersistence
import com.example.warehouse.persistence.ProductPersistence
import com.example.warehouse.persistence.WarehousePersistence
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryStockPersistence: InventoryStockPersistence,
    private val warehousePersistence: WarehousePersistence,
    private val productPersistence: ProductPersistence
) {

    fun addProductToWarehouse(request: AddInventoryRequest): InventoryResponse {

        val warehouse = warehousePersistence.findById(request.warehouseId)
            .orElseThrow { NotFoundException("Warehouse not found") }

        val product = productPersistence.findById(request.productId)
            .orElseThrow { NotFoundException("Product not found") }

        if (
            inventoryStockPersistence.findByWarehouseIdAndProductId(
                warehouse.id, product.id
            ) != null
        ) {
            throw ConflictException("Product already exists in warehouse")
        }

        val inventory = inventoryStockPersistence.save(
            InventoryStock(
                warehouse = warehouse,
                product = product,
                stockQuantity = request.quantity,
                reorderThreshold = request.reorderThreshold
            )
        )

        return inventory.toResponse()
    }

    fun updateStock(
        warehouseId: Long,
        productId: Long,
        request: UpdateStockRequest
    ): InventoryResponse {

        val inventory = inventoryStockPersistence
            .findByWarehouseIdAndProductId(warehouseId, productId)
            ?: throw NotFoundException("Inventory record not found")

        val newQty = inventory.stockQuantity + request.quantity
        if (newQty < 0) {
            throw BadRequestException("Insufficient stock")
        }

        inventory.stockQuantity = newQty
        return inventoryStockPersistence.save(inventory).toResponse()
    }

    fun getInventoryByWarehouse(warehouseId: Long): List<InventoryResponse> {

        if (!warehousePersistence.existsById(warehouseId)) {
            throw NotFoundException("Warehouse not found")
        }

        val inventorystocklist = inventoryStockPersistence.findByWarehouseId(warehouseId)
        println(inventorystocklist)
        return inventorystocklist
            .map { it.toResponse() }
    }

    fun deductStock(
        warehouseId: Long,
        items: List<OrderItem>
    ) {
        items.forEach { item ->
            val stock = inventoryStockPersistence
                .findByWarehouseIdAndProductId(
                    warehouseId,
                    item.product.id
                )
                ?: throw IllegalStateException(
                    "No inventory for product ${item.product.name}"
                )

            if (stock.stockQuantity < item.quantity) {
                throw IllegalStateException(
                    "Insufficient stock for product ${item.product.name}"
                )
            }

            stock.stockQuantity -= item.quantity
        }
    }

    fun rollbackStock(
        warehouseId: Long,
        items: List<OrderItem>
    ) {
        items.forEach { item ->
            val stock = inventoryStockPersistence
                .findByWarehouseIdAndProductId(
                    warehouseId,
                    item.product.id)
                ?: throw IllegalStateException(
                    "No inventory for product ${item.product.name}"
                )

            stock.stockQuantity += item.quantity
        }
    }

}



fun InventoryStock.toResponse() = InventoryResponse(
    id = id,
    warehouseId = warehouse.id,
    productId = product.id,
    productName = product.name,
    sku = product.sku,
    quantity = stockQuantity,
    reorderThreshold = reorderThreshold
)