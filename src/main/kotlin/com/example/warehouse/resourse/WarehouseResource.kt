package com.example.warehouse.resourse


import com.example.warehouse.dto.CreateWarehouseRequest
import com.example.warehouse.dto.UpdateWarehouseRequest
import com.example.warehouse.dto.WarehouseResponse
import com.example.warehouse.service.WarehouseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/warehouses")
class WarehouseResource(
    private val warehouseService: WarehouseService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateWarehouseRequest): WarehouseResponse =
        warehouseService.create(request)

    @GetMapping
    fun getAll(): List<WarehouseResponse> =
        warehouseService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): WarehouseResponse {
        println("controller")
        return warehouseService.getById(id)
    }
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateWarehouseRequest
    ): WarehouseResponse =
        warehouseService.update(id, request)
}
