package com.example.warehouse.service

import com.example.warehouse.dto.AddInventoryRequest
import com.example.warehouse.dto.UpdateStockRequest
import com.example.warehouse.exception.BadRequestException
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Customer
import com.example.warehouse.model.InventoryStock
import com.example.warehouse.model.Order
import com.example.warehouse.model.OrderItem
import com.example.warehouse.model.OrderStatus
import com.example.warehouse.model.Product
import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.model.Warehouse

import com.example.warehouse.persistence.InventoryStockPersistence
import com.example.warehouse.persistence.ProductPersistence
import com.example.warehouse.persistence.WarehousePersistence
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class InventoryServiceTest {

    private val inventoryPersistence: InventoryStockPersistence = mockk()
    private val warehousePersistence: WarehousePersistence = mockk()
    private val productPersistence: ProductPersistence = mockk()

    private val inventoryService = InventoryService(
        inventoryPersistence,
        warehousePersistence,
        productPersistence
    )

    private fun warehouse(
        id: Long = 1L,
        manager: User = user()
    ) = Warehouse(
        id = id,
        name = "Test Warehouse",
        location = "Indore",
        code = "WH-1",
        capacity = 1000,
        manager = manager
    )

    private fun user(
        id: Long = 1L,
        role: Role = Role.WAREHOUSE_MANAGER
    ) = User(
        id = id,
        name = "Manager",
        email = "manager@test.com",
        password = "pass",
        role = role,
        enabled = true
    )

    private fun product(
        id: Long = 1L,
        sku: String = "SKU-1001",
        name: String = "Test Product",
        price: Double = 999.0
    ) = Product(
        id = id,
        sku = sku,
        name = name,
        price = price
    )

    @Test
    fun `add product to warehouse success`() {
        val warehouse = warehouse(id = 1L)
        val product = Product(id = 1L, sku = "SKU1", name = "Laptop", price = 50000.0)

        every { warehousePersistence.findById(1L) } returns Optional.of(warehouse)
        every { productPersistence.findById(1L) } returns Optional.of(product)
        every {
            inventoryPersistence.findByWarehouseIdAndProductId(1L, 1L)
        } returns null

        every { inventoryPersistence.save(any()) } answers { firstArg() }

        inventoryService.addProductToWarehouse(
            AddInventoryRequest(
                warehouseId = 1L,
                productId = 1L,
                quantity = 10,
                reorderThreshold = 2
            )
        )

        verify(exactly = 1) { inventoryPersistence.save(any()) }
    }

    @Test
    fun `add product throws conflict if inventory exists`() {
        val warehouse = warehouse(id = 1L)
        val product = product(id = 1L)

        every { warehousePersistence.findById(1L) } returns Optional.of(warehouse)
        every { productPersistence.findById(1L) } returns Optional.of(product)
        every {
            inventoryPersistence.findByWarehouseIdAndProductId(1L, 1L)
        } returns InventoryStock(1L, warehouse,product,100, 80)

        assertThrows<ConflictException> {
            inventoryService.addProductToWarehouse(
                AddInventoryRequest(1L, 1L, 5, 1)
            )
        }
    }

    @Test
    fun `update stock throws bad request if insufficient stock`() {
        val inventory = InventoryStock(1L, warehouse(),product(),0, 80)

        every {
            inventoryPersistence.findByWarehouseIdAndProductId(1L, 1L)
        } returns inventory

        assertThrows<BadRequestException> {
            inventoryService.updateStock(
                1L,
                1L,
                UpdateStockRequest(quantity = -5)
            )
        }
    }

    @Test
    fun `get inventory throws not found if warehouse missing`() {
        every { warehousePersistence.existsById(1L) } returns false

        assertThrows<EntityNotFoundException> {
            inventoryService.getInventoryByWarehouse(1L)
        }
    }

    @Test
    fun `deduct stock throws exception if insufficient quantity`() {
        val product = product(1L)
        val orderItem = OrderItem(product = product, quantity = 5, order = order(), price = 1000.00)

        val stock = InventoryStock(
            product = product,
            stockQuantity = 2,
            warehouse = warehouse(1L),
            reorderThreshold = 1
        )

        every {
            inventoryPersistence.findByWarehouseIdAndProductId(1L, 1L)
        } returns stock

        assertThrows<IllegalStateException> {
            inventoryService.deductStock(1L, listOf(orderItem))
        }
    }

    private fun order(
        id: Long = 1L,
        warehouse: Warehouse = warehouse(),
        customer: Customer = customer(),
        totalAmount: Double = 1000.0,
        status: OrderStatus = OrderStatus.CREATED
    ) = Order(
        id = id,
        warehouse = warehouse,
        customer = customer,
        status = status,
        totalAmount = totalAmount
    )

    private fun customer() = Customer(
        name = "Test Customer",
        phone = "9999999999",
        email = "customer@test.com",
        address = "Indore"
    )
}