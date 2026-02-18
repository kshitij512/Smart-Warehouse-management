package com.example.warehouse.resourse


import com.example.warehouse.dto.AddInventoryRequest
import com.example.warehouse.dto.InventoryResponse
import com.example.warehouse.dto.UpdateStockRequest
import com.example.warehouse.service.InventoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inventory")
class InventoryStockResource(
    private val inventoryService: InventoryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addProduct(@Valid @RequestBody request: AddInventoryRequest): InventoryResponse =
        inventoryService.addProductToWarehouse(request)

    @PatchMapping("/warehouse/{warehouseId}/product/{productId}")
    fun updateStock(
        @PathVariable warehouseId: Long,
        @PathVariable productId: Long,
        @RequestBody request: UpdateStockRequest
    ): InventoryResponse =
        inventoryService.updateStock(warehouseId, productId, request)

    @GetMapping("/warehouse/{warehouseId}")
    fun getByWarehouse(
        @PathVariable warehouseId: Long
    ): List<InventoryResponse> =
        inventoryService.getInventoryByWarehouse(warehouseId)
}
