package com.example.warehouse.resourse

import com.example.warehouse.dto.CreateWarehouseRequest
import com.example.warehouse.dto.UpdateWarehouseRequest
import com.example.warehouse.dto.WarehouseResponse
import com.example.warehouse.service.WarehouseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Warehouses",
    description = "APIs for managing warehouses"
)
@RestController
@RequestMapping("/api/warehouses")
class WarehouseResource(
    private val warehouseService: WarehouseService
) {

    @Operation(
        summary = "Create a warehouse",
        description = "Creates a new warehouse with a unique warehouse code and an assigned warehouse manager."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Warehouse created successfully"),
            ApiResponse(responseCode = "409", description = "Warehouse code already exists or invalid manager")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateWarehouseRequest
    ): WarehouseResponse =
        warehouseService.create(request)

    @Operation(
        summary = "Get all warehouses",
        description = "Returns a list of all warehouses."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully")
        ]
    )
    @GetMapping
    fun getAll(): List<WarehouseResponse> =
        warehouseService.getAll()

    @Operation(
        summary = "Get warehouse by ID",
        description = "Fetches warehouse details using warehouse ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Warehouse found"),
            ApiResponse(responseCode = "404", description = "Warehouse not found")
        ]
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): WarehouseResponse {
        println("controller")
        return warehouseService.getById(id)
    }

    @Operation(
        summary = "Update warehouse",
        description = "Updates warehouse details such as name, location, capacity, or manager."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Warehouse updated successfully"),
            ApiResponse(responseCode = "404", description = "Warehouse not found"),
            ApiResponse(responseCode = "409", description = "Invalid manager assignment")
        ]
    )
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateWarehouseRequest
    ): WarehouseResponse =
        warehouseService.update(id, request)
}