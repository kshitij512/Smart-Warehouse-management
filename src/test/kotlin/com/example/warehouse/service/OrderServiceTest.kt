package com.example.warehouse.service

import com.example.warehouse.dto.*
import com.example.warehouse.model.*
import com.example.warehouse.persistence.*
import io.mockk.Runs
import io.mockk.every

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class OrderServiceTest {

    /* ================= MOCKS ================= */

    @MockK lateinit var inventoryService: InventoryService
    @MockK lateinit var orderPersistence: OrderPersistence
    @MockK lateinit var orderItemPersistence: OrderItemPersistence
    @MockK lateinit var warehousePersistence: WarehousePersistence
    @MockK lateinit var productPersistence: ProductPersistence
    @MockK lateinit var userPersistence: UserPersistence
    @MockK lateinit var orderTrackingPersistence: OrderTrackingPersistence

    @InjectMockKs
    lateinit var orderService: OrderService

    /* ================= TESTS ================= */

    @Test
    fun `createOrder should create order successfully`() {
        val warehouse = warehouse()
        val product = product(price = 100.0)

        every { warehousePersistence.findByIdOrNull(1L) } returns warehouse
        every { productPersistence.findByIdOrNull(1L) } returns product
        every { orderPersistence.save(any()) } answers { firstArg() }
        every { orderItemPersistence.save(any()) } answers { firstArg() }
        every { orderTrackingPersistence.save(any()) } answers { firstArg() }

        val request = CreateOrderRequest(
            warehouseId = 1L,
            customerName = "John",
            customerPhone = "9999999999",
            customerEmail = "john@mail.com",
            customerAddress = "Indore",
            items = listOf(
                OrderItemRequest(productId = 1L, quantity = 2)
            )
        )

        val response = orderService.createOrder(request)

        assertEquals(OrderStatus.CREATED, response.status)
        assertEquals(200.0, response.totalAmount)
    }

    @Test
    fun `getById should throw exception when order not found`() {
        every { orderPersistence.findByIdOrNull(1L) } returns null

        assertThrows(EntityNotFoundException::class.java) {
            orderService.getById(1L)
        }
    }

    @Test
    fun `assignStaff should assign staff`() {
        val order = order()
        val staff = user(role = Role.STAFF)

        every { orderPersistence.findByIdOrNull(1L) } returns order
        every { userPersistence.findByIdOrNull(2L) } returns staff

        orderService.assignStaff(1L, 2L)

        assertEquals(staff, order.assignedStaff)
    }

    @Test
    fun `assignStaff should fail if role is not STAFF`() {
        val order = order()
        val manager = user(role = Role.WAREHOUSE_MANAGER)

        every { orderPersistence.findByIdOrNull(1L) } returns order
        every { userPersistence.findByIdOrNull(2L) } returns manager

        assertThrows(IllegalStateException::class.java) {
            orderService.assignStaff(1L, 2L)
        }
    }

    @Test
    fun `updateStatus should confirm order and deduct stock`() {
        val order = order(status = OrderStatus.CREATED)
        val tracking = tracking(order)

        every { orderPersistence.findByIdOrNull(1L) } returns order
        every { orderTrackingPersistence.findByOrderId(1L) } returns tracking
        every { inventoryService.deductStock(any(), any()) } just Runs

        orderService.updateStatus(1L, OrderStatus.CONFIRMED)

        assertEquals(OrderStatus.CONFIRMED, order.status)
        assertEquals(OrderStatus.CONFIRMED, tracking.currentStatus)
    }

    @Test
    fun `updateStatus should fail for invalid transition`() {
        val order = order(status = OrderStatus.CREATED)
        val tracking = tracking(order)

        every { orderPersistence.findByIdOrNull(1L) } returns order
        every { orderTrackingPersistence.findByOrderId(1L) } returns tracking

        assertThrows(IllegalStateException::class.java) {
            orderService.updateStatus(1L, OrderStatus.SHIPPED)
        }
    }

    @Test
    fun `getTracking should return empty response when not found`() {
        every { orderTrackingPersistence.findByOrderId(1L) } returns null

        val response = orderService.getTracking(1L)

        assertNull(response.currentStatus)
    }

    /* ================= TEST DATA FACTORIES ================= */

    private fun user(
        id: Long = 1L,
        role: Role = Role.STAFF
    ) = User(
        id = id,
        name = "User",
        email = "user@mail.com",
        password = "pass",
        role = role,
        enabled = true
    )

    private fun warehouse() = Warehouse(
        id = 1L,
        name = "Warehouse",
        location = "Indore",
        code = "WH-1",
        capacity = 1000,
        manager = user(role = Role.WAREHOUSE_MANAGER)
    )

    private fun product(price: Double = 100.0) = Product(
        id = 1L,
        sku = "SKU-1",
        name = "Product",
        price = price
    )

    private fun order(
        status: OrderStatus = OrderStatus.CREATED
    ) = Order(
        id = 1L,
        warehouse = warehouse(),
        customer = Customer(
            name = "Customer",
            phone = "9999999999",
            email = "cust@mail.com",
            address = "Indore"
        ),
        status = status,
        totalAmount = 100.0
    )

    private fun tracking(order: Order) = OrderTracking(
        order = order,
        currentStatus = order.status,
        createdAt = LocalDateTime.now(),
        confirmedAt = null,
        pickingAt = null,
        packedAt = null,
        shippedAt = null,
        deliveredAt = null,
        cancelledAt = null
    )
}