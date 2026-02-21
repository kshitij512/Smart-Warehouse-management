package com.example.warehouse.service

import com.example.warehouse.dto.AddInventoryRequest
import com.example.warehouse.dto.InventoryResponse
import com.example.warehouse.dto.UpdateStockRequest
import com.example.warehouse.exception.BadRequestException
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.InventoryStock
import com.example.warehouse.model.OrderItem
import com.example.warehouse.persistence.InventoryStockPersistence
import com.example.warehouse.persistence.ProductPersistence
import com.example.warehouse.persistence.WarehousePersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service responsible for inventory and stock management operations.
 * Handles stock creation, updates, deduction, and rollback.
 */
@Service
class InventoryService(
    private val inventoryStockPersistence: InventoryStockPersistence,
    private val warehousePersistence: WarehousePersistence,
    private val productPersistence: ProductPersistence
) {

    private val log = LoggerFactory.getLogger(InventoryService::class.java)

    /**
     * Adds a product to a warehouse inventory.
     */
    fun addProductToWarehouse(request: AddInventoryRequest): InventoryResponse {

        log.info(
            "Add inventory request received | warehouseId={}, productId={}",
            request.warehouseId,
            request.productId
        )

        val warehouse = warehousePersistence.findById(request.warehouseId)
            .orElseThrow {
                log.warn("Warehouse not found with ID {}", request.warehouseId)
                EntityNotFoundException("Warehouse not found")
            }

        val product = productPersistence.findById(request.productId)
            .orElseThrow {
                log.warn("Product not found with ID {}", request.productId)
                EntityNotFoundException("Product not found")
            }

        // Check if inventory already exists
        if (
            inventoryStockPersistence.findByWarehouseIdAndProductId(
                warehouse.id,
                product.id
            ) != null
        ) {
            log.warn(
                "Inventory already exists | warehouseId={}, productId={}",
                warehouse.id,
                product.id
            )
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

        log.info(
            "Inventory created successfully | inventoryId={}, quantity={}",
            inventory.id,
            inventory.stockQuantity
        )

        return inventory.toResponse()
    }

    /**
     * Updates stock quantity for a product in a warehouse.
     */
    fun updateStock(
        warehouseId: Long,
        productId: Long,
        request: UpdateStockRequest
    ): InventoryResponse {

        log.info(
            "Update stock request | warehouseId={}, productId={}, changeQty={}",
            warehouseId,
            productId,
            request.quantity
        )

        val inventory = inventoryStockPersistence
            .findByWarehouseIdAndProductId(warehouseId, productId)
            ?: run {
                log.warn(
                    "Inventory record not found | warehouseId={}, productId={}",
                    warehouseId,
                    productId
                )
                throw EntityNotFoundException("Inventory record not found")
            }

        val newQty = inventory.stockQuantity + request.quantity
        if (newQty < 0) {
            log.warn(
                "Insufficient stock | productId={}, currentQty={}, requestedChange={}",
                productId,
                inventory.stockQuantity,
                request.quantity
            )
            throw BadRequestException("Insufficient stock")
        }

        inventory.stockQuantity = newQty

        log.info(
            "Stock updated successfully | productId={}, newQty={}",
            productId,
            newQty
        )

        return inventoryStockPersistence.save(inventory).toResponse()
    }

    /**
     * Retrieves inventory list for a specific warehouse.
     */
    fun getInventoryByWarehouse(warehouseId: Long): List<InventoryResponse> {

        log.debug("Fetching inventory for warehouseId={}", warehouseId)

        if (!warehousePersistence.existsById(warehouseId)) {
            log.warn("Warehouse not found while fetching inventory | warehouseId={}", warehouseId)
            throw EntityNotFoundException("Warehouse not found")
        }

        val inventorystocklist =
            inventoryStockPersistence.findByWarehouseId(warehouseId)

        // Existing debug print retained as requested
        println(inventorystocklist)

        log.info(
            "Inventory fetched successfully | warehouseId={}, totalItems={}",
            warehouseId,
            inventorystocklist.size
        )

        return inventorystocklist
            .map { it.toResponse() }
    }

    /**
     * Deducts stock for order confirmation.
     */
    fun deductStock(
        warehouseId: Long,
        items: List<OrderItem>
    ) {

        log.info(
            "Deducting stock for warehouseId={}, totalItems={}",
            warehouseId,
            items.size
        )

        items.forEach { item ->
            val stock = inventoryStockPersistence
                .findByWarehouseIdAndProductId(
                    warehouseId,
                    item.product.id
                )
                ?: run {
                    log.error(
                        "No inventory found for product {} in warehouse {}",
                        item.product.name,
                        warehouseId
                    )
                    throw IllegalStateException(
                        "No inventory for product ${item.product.name}"
                    )
                }

            if (stock.stockQuantity < item.quantity) {
                log.error(
                    "Insufficient stock | product={}, available={}, required={}",
                    item.product.name,
                    stock.stockQuantity,
                    item.quantity
                )
                throw IllegalStateException(
                    "Insufficient stock for product ${item.product.name}"
                )
            }

            stock.stockQuantity -= item.quantity

            log.debug(
                "Stock deducted | product={}, remainingQty={}",
                item.product.name,
                stock.stockQuantity
            )
        }
    }

    /**
     * Rolls back stock in case of order cancellation.
     */
    fun rollbackStock(
        warehouseId: Long,
        items: List<OrderItem>
    ) {

        log.info(
            "Rolling back stock for warehouseId={}, totalItems={}",
            warehouseId,
            items.size
        )

        items.forEach { item ->
            val stock = inventoryStockPersistence
                .findByWarehouseIdAndProductId(
                    warehouseId,
                    item.product.id
                )
                ?: run {
                    log.error(
                        "No inventory found for rollback | product={}, warehouseId={}",
                        item.product.name,
                        warehouseId
                    )
                    throw IllegalStateException(
                        "No inventory for product ${item.product.name}"
                    )
                }

            stock.stockQuantity += item.quantity

            log.debug(
                "Stock rollback completed | product={}, newQty={}",
                item.product.name,
                stock.stockQuantity
            )
        }
    }
}

/**
 * Extension function to convert InventoryStock entity into InventoryResponse DTO.
 */
fun InventoryStock.toResponse() = InventoryResponse(
    id = id,
    warehouseId = warehouse.id,
    productId = product.id,
    productName = product.name,
    sku = product.sku,
    quantity = stockQuantity,
    reorderThreshold = reorderThreshold
)
