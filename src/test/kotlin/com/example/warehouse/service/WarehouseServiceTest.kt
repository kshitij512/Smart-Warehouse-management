package com.example.warehouse.service

import com.example.warehouse.dto.CreateWarehouseRequest
import com.example.warehouse.dto.UpdateWarehouseRequest
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.model.Warehouse
import com.example.warehouse.persistence.UserPersistence
import com.example.warehouse.persistence.WarehousePersistence
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
class WarehouseServiceTest {

    @MockK lateinit var warehousePersistence: WarehousePersistence
    @MockK lateinit var userPersistence: UserPersistence

    private lateinit var service: WarehouseService

    @BeforeEach
    fun setup() {
        service = WarehouseService(warehousePersistence, userPersistence)
    }

    // ---------- CREATE ----------

    @Test
    fun `create warehouse success`() {
        val manager = mockManager()
        val warehouse = mockWarehouse(manager)

        every { warehousePersistence.existsByCode("WH-1") } returns false
        every { userPersistence.findByIdOrNull(1) } returns manager
        every { warehousePersistence.save(any()) } returns warehouse

        val result = service.create(
            CreateWarehouseRequest(
                name = "Main",
                location = "Delhi",
                code = "WH-1",
                capacity = 1000,
                managerId = 1
            )
        )

        assertEquals("WH-1", result.code)
        assertEquals("Manager", result.managerName)
    }

    @Test
    fun `create fails when code exists`() {
        every { warehousePersistence.existsByCode(any()) } returns true

        assertThrows<ConflictException> {
            service.create(mockCreateRequest())
        }
    }

    @Test
    fun `create fails when manager not found`() {
        every { warehousePersistence.existsByCode(any()) } returns false
        every { userPersistence.findByIdOrNull(any()) } returns null

        assertThrows<EntityNotFoundException> {
            service.create(mockCreateRequest())
        }
    }

    @Test
    fun `create fails when user is not manager`() {
        val user = mockStaff()

        every { warehousePersistence.existsByCode(any()) } returns false
        every { userPersistence.findByIdOrNull(any()) } returns user

        assertThrows<ConflictException> {
            service.create(mockCreateRequest())
        }
    }

    // ---------- GET ----------

    @Test
    fun `get all warehouses`() {
        every { warehousePersistence.findAll() } returns listOf(mockWarehouse(mockManager()))

        val result = service.getAll()

        assertEquals(1, result.size)
    }

    @Test
    fun `get by id fails when not found`() {
        every { warehousePersistence.findByIdOrNull(any()) } returns null

        assertThrows<EntityNotFoundException> {
            service.getById(1)
        }
    }

    // ---------- UPDATE ----------

    @Test
    fun `update warehouse success`() {
        val manager = mockManager()
        val warehouse = mockWarehouse(manager)

        every { warehousePersistence.findByIdOrNull(1) } returns warehouse
        every { warehousePersistence.save(any()) } returns warehouse

        val result = service.update(
            1,
            UpdateWarehouseRequest(
                name = "Updated",
                location = null,
                capacity = 2000,
                managerId = null
            )
        )

        assertEquals("Updated", result.name)
        assertEquals(2000, result.capacity)
    }

    @Test
    fun `update fails when manager role invalid`() {
        val warehouse = mockWarehouse(mockManager())
        val invalidUser = mockStaff()

        every { warehousePersistence.findByIdOrNull(1) } returns warehouse
        every { userPersistence.findByIdOrNull(2) } returns invalidUser

        assertThrows<ConflictException> {
            service.update(
                1,
                UpdateWarehouseRequest(name = "warehouse 1", location = "Mumbai", managerId = 2, capacity = 100)
            )
        }
    }

    // ---------- Helpers ----------

    private fun mockManager() =
        User(
            id = 1,
            name = "Manager",
            email = "manager@mail.com",
            role = Role.WAREHOUSE_MANAGER,
            password = "pass",
            enabled = true
        )

    private fun mockStaff() =
        User(
            id = 2,
            name = "Staff",
            email = "staff@mail.com",
            role = Role.STAFF,
            password = "pass",
            enabled = true
        )

    private fun mockWarehouse(manager: User) =
        Warehouse(
            id = 1,
            name = "WH",
            location = "City",
            code = "WH-1",
            capacity = 1000,
            manager = manager
        )

    private fun mockCreateRequest() =
        CreateWarehouseRequest(
            name = "WH",
            location = "City",
            code = "WH-1",
            capacity = 1000,
            managerId = 1
        )
}