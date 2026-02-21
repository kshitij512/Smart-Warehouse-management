package com.example.warehouse.service

import com.example.warehouse.persistence.WarehousePersistence
import com.example.warehouse.dto.*
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Role
import com.example.warehouse.model.Warehouse
import com.example.warehouse.persistence.UserPersistence
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * Service responsible for warehouse management operations
 * such as creation, retrieval, and updates.
 */
@Service
class WarehouseService(
    private val warehousePersistence: WarehousePersistence,
    private val userPersistence: UserPersistence
) {

    private val log = LoggerFactory.getLogger(WarehouseService::class.java)

    /**
     * Creates a new warehouse after validating uniqueness
     * and manager role eligibility.
     */
    fun create(request: CreateWarehouseRequest): WarehouseResponse {

        log.info("Create warehouse request received with code: {}", request.code)

        // Validate warehouse code uniqueness
        if (warehousePersistence.existsByCode(request.code)) {
            log.warn("Warehouse creation failed: Code already exists [{}]", request.code)
            throw ConflictException("Warehouse code already exists")
        }

        // Fetch manager user
        val manager = userPersistence.findByIdOrNull(request.managerId)
            ?: run {
                log.warn("Warehouse creation failed: Manager not found with ID {}", request.managerId)
                throw EntityNotFoundException("Manager not found")
            }

        // Validate manager role
        if (manager.role != Role.WAREHOUSE_MANAGER) {
            log.warn(
                "Warehouse creation failed: User {} is not a WAREHOUSE_MANAGER",
                manager.email
            )
            throw ConflictException("User is not a MANAGER")
        }

        // Create warehouse entity
        val warehouse = Warehouse(
            name = request.name,
            location = request.location,
            code = request.code,
            capacity = request.capacity,
            manager = manager
        )

        val savedWarehouse = warehousePersistence.save(warehouse)

        log.info(
            "Warehouse created successfully with ID: {} and code: {}",
            savedWarehouse.id,
            savedWarehouse.code
        )

        return savedWarehouse.toResponse()
    }

    /**
     * Retrieves all warehouses.
     */
    fun getAll(): List<WarehouseResponse> {

        log.debug("Fetching all warehouses")

        val warehouses = warehousePersistence.findAll().map { it.toResponse() }

        log.info("Total warehouses fetched: {}", warehouses.size)

        return warehouses
    }

    /**
     * Retrieves a warehouse by its ID.
     */
    fun getById(id: Long): WarehouseResponse {

        log.debug("Fetching warehouse by ID: {}", id)

        return warehousePersistence.findByIdOrNull(id)
            ?.toResponse()
            ?: run {
                log.warn("Warehouse not found with ID: {}", id)
                throw com.example.warehouse.exception.EntityNotFoundException("Warehouse not found")
            }
    }

    /**
     * Updates warehouse details including optional manager reassignment.
     */
    fun update(id: Long, request: UpdateWarehouseRequest): WarehouseResponse {

        log.info("Update warehouse request received for ID: {}", id)

        val warehouse = warehousePersistence.findByIdOrNull(id)
            ?: run {
                log.warn("Warehouse update failed: Warehouse not found with ID {}", id)
                throw EntityNotFoundException("Warehouse not found")
            }

        // Apply partial updates
        request.name?.let {
            log.debug("Updating warehouse name for ID: {}", id)
            warehouse.name = it
        }

        request.location?.let {
            log.debug("Updating warehouse location for ID: {}", id)
            warehouse.location = it
        }

        request.capacity?.let {
            log.debug("Updating warehouse capacity for ID: {}", id)
            warehouse.capacity = it
        }

        // Handle optional manager update
        request.managerId?.let {
            log.debug("Updating warehouse manager for warehouse ID: {}", id)

            val manager = userPersistence.findByIdOrNull(it)
                ?: run {
                    log.warn("Warehouse update failed: Manager not found with ID {}", it)
                    throw EntityNotFoundException("Manager not found")
                }

            if (manager.role != Role.WAREHOUSE_MANAGER) {
                log.warn(
                    "Warehouse update failed: User {} is not a WAREHOUSE_MANAGER",
                    manager.email
                )
                throw ConflictException("User is not a MANAGER")
            }

            warehouse.manager = manager
        }

        val updatedWarehouse = warehousePersistence.save(warehouse)

        log.info("Warehouse updated successfully for ID: {}", id)

        return updatedWarehouse.toResponse()
    }
}

/**
 * Extension function to convert Warehouse entity to WarehouseResponse DTO.
 */
fun Warehouse.toResponse() = WarehouseResponse(
    id = id,
    name = name,
    location = location,
    code = code,
    capacity = capacity,
    managerName = manager.name
)
