package com.example.warehouse.service

import com.example.warehouse.persistence.WarehousePersistence

import com.example.warehouse.dto.*
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.NotFoundException
import com.example.warehouse.model.Role
import com.example.warehouse.model.Warehouse
import com.example.warehouse.persistence.UserPersistence
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class WarehouseService(
    private val warehousePersistence: WarehousePersistence,
    private val userPersistence: UserPersistence
) {

    fun create(request: CreateWarehouseRequest): WarehouseResponse {

        if (warehousePersistence.existsByCode(request.code)) {
            throw ConflictException("Warehouse code already exists")
        }

        val manager = userPersistence.findByIdOrNull(request.managerId)
            ?: throw EntityNotFoundException("Manager not found")

        if (manager.role != Role.WAREHOUSE_MANAGER) {
            throw ConflictException("User is not a MANAGER")
        }

        val warehouse = Warehouse(
            name = request.name,
            location = request.location,
            code = request.code,
            capacity = request.capacity,
            manager = manager
        )

        return warehousePersistence.save(warehouse).toResponse()
    }

    fun getAll(): List<WarehouseResponse> =
        warehousePersistence.findAll().map { it.toResponse() }

    fun getById(id: Long): WarehouseResponse =
        warehousePersistence.findByIdOrNull(id)
            ?.toResponse()
            ?: throw NotFoundException("Warehouse not found")

    fun update(id: Long, request: UpdateWarehouseRequest): WarehouseResponse {

        val warehouse = warehousePersistence.findByIdOrNull(id)
            ?: throw NotFoundException("Warehouse not found")

        request.name?.let { warehouse.name = it }
        request.location?.let { warehouse.location = it }
        request.capacity?.let { warehouse.capacity = it }

        request.managerId?.let {
            val manager = userPersistence.findByIdOrNull(it)
                ?: throw EntityNotFoundException("Manager not found")

            if (manager.role != Role.WAREHOUSE_MANAGER) {
                throw ConflictException("User is not a MANAGER")
            }

            warehouse.manager = manager
        }

        return warehousePersistence.save(warehouse).toResponse()
    }
}


fun Warehouse.toResponse() = WarehouseResponse(
    id = id,
    name = name,
    location = location,
    code = code,
    capacity = capacity,
    managerName = manager.name
)
