package com.example.warehouse.service

import com.example.warehouse.dto.*
import com.example.warehouse.model.*
import com.example.warehouse.persistence.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class OrderService(
    private val inventoryService: InventoryService,
    private val orderPersistence: OrderPersistence,
    private val orderItemPersistence: OrderItemPersistence,
    private val warehousePersistence: WarehousePersistence,
    private val productPersistence: ProductPersistence,
    private val userPersistence: UserPersistence,
    private val orderTrackingPersistence: OrderTrackingPersistence
) {

    /* ============================================================
       🚀 STATUS WORKFLOW ENGINE (STRICT TRANSITIONS)
       ============================================================ */

    private val validTransitions = mapOf(
        OrderStatus.CREATED to listOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
        OrderStatus.CONFIRMED to listOf(OrderStatus.PICKING, OrderStatus.CANCELLED),
        OrderStatus.PICKING to listOf(OrderStatus.PACKED, OrderStatus.CANCELLED),
        OrderStatus.PACKED to listOf(OrderStatus.SHIPPED),
        OrderStatus.SHIPPED to listOf(OrderStatus.DELIVERED),
        OrderStatus.DELIVERED to emptyList(),
        OrderStatus.CANCELLED to emptyList()
    )

    /* ============================================================
       CREATE ORDER
       ============================================================ */

    fun createOrder(request: CreateOrderRequest): OrderResponse {

        val warehouse = warehousePersistence.findByIdOrNull(request.warehouseId)
            ?: throw EntityNotFoundException("Warehouse not found")

        val order = Order(
            warehouse = warehouse,
            customer = Customer(
                name = request.customerName,
                phone = request.customerPhone,
                email = request.customerEmail,
                address = request.customerAddress
            ),
            status = OrderStatus.CREATED,
            totalAmount = 0.0
        )

        orderPersistence.save(order)

        var total = 0.0

        request.items.forEach {

            val product = productPersistence.findByIdOrNull(it.productId)
                ?: throw EntityNotFoundException("Product not found")

            total += product.price * it.quantity

            orderItemPersistence.save(
                OrderItem(
                    order = order,
                    product = product,
                    quantity = it.quantity,
                    price = product.price
                )
            )
        }

        order.totalAmount = total

        val tracking = OrderTracking(
            order = order,
            currentStatus = OrderStatus.CREATED,
            createdAt = LocalDateTime.now(),
            confirmedAt = null,
            pickingAt = null,
            packedAt = null,
            shippedAt = null,
            deliveredAt = null,
            cancelledAt = null
        )

        orderTrackingPersistence.save(tracking)

        return order.toResponse(validTransitions)
    }

    /* ============================================================
       GETTERS
       ============================================================ */

    fun getById(orderId: Long): OrderResponse =
        orderPersistence.findByIdOrNull(orderId)
            ?.toResponse(validTransitions)
            ?: throw EntityNotFoundException("Order not found")

    fun getByWarehouse(warehouseId: Long): List<OrderResponse> =
        orderPersistence.findAllByWarehouseId(warehouseId)
            .map { it.toResponse(validTransitions) }

    fun getByStatus(status: OrderStatus): List<OrderResponse> =
        orderPersistence.findAllByStatus(status)
            .map { it.toResponse(validTransitions) }

    /* ============================================================
       ASSIGN STAFF
       ============================================================ */

    fun assignStaff(orderId: Long, staffId: Long) {

        val order = orderPersistence.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("Order not found")

        if (order.assignedStaff != null) {
            throw IllegalStateException("Staff already assigned to this order")
        }

        val staff = userPersistence.findByIdOrNull(staffId)
            ?: throw EntityNotFoundException("Staff not found")

        if (staff.role != Role.STAFF) {
            throw IllegalStateException("Only STAFF can be assigned")
        }

        order.assignedStaff = staff
    }

    /* ============================================================
       🔥 STRICT WORKFLOW STATUS UPDATE
       ============================================================ */

    fun updateStatus(orderId: Long, newStatus: OrderStatus) {

        val order = orderPersistence.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("Order not found")

        val tracking = orderTrackingPersistence.findByOrderId(orderId)
            ?: throw EntityNotFoundException("Tracking not found")

        val currentStatus = order.status

        // 🚫 Prevent illegal transitions
        val allowedTransitions = validTransitions[currentStatus]
            ?: emptyList()

        if (!allowedTransitions.contains(newStatus)) {
            throw IllegalStateException(
                "Invalid status transition: $currentStatus → $newStatus"
            )
        }

        when (newStatus) {

            OrderStatus.CONFIRMED -> {
                inventoryService.deductStock(order.warehouse.id, order.items)
                tracking.confirmedAt = LocalDateTime.now()
            }

            OrderStatus.CANCELLED -> {
                if (currentStatus != OrderStatus.CREATED) {
                    inventoryService.rollbackStock(order.warehouse.id, order.items)
                }
                tracking.cancelledAt = LocalDateTime.now()
            }

            OrderStatus.PICKING ->
                tracking.pickingAt = LocalDateTime.now()

            OrderStatus.PACKED ->
                tracking.packedAt = LocalDateTime.now()

            OrderStatus.SHIPPED ->
                tracking.shippedAt = LocalDateTime.now()

            OrderStatus.DELIVERED ->
                tracking.deliveredAt = LocalDateTime.now()

            else -> {}
        }

        // ✅ Keep both in sync
        tracking.currentStatus = newStatus
        order.status = newStatus
    }

    /* ============================================================
       TRACKING FETCH (SAFE)
       ============================================================ */

    fun getTracking(orderId: Long): OrderTrackingResponse {

        val tracking = orderTrackingPersistence.findByOrderId(orderId)
            ?: return OrderTrackingResponse(
                currentStatus = null,
                createdAt = null,
                confirmedAt = null,
                pickingAt = null,
                packedAt = null,
                shippedAt = null,
                deliveredAt = null,
                cancelledAt = null
            )

        return tracking.toResponse()
    }
}

/* ============================================================
   MAPPERS
   ============================================================ */
fun Order.toResponse(
    validTransitions: Map<OrderStatus, List<OrderStatus>>
): OrderResponse =
    OrderResponse(
        id = id,
        warehouseId = warehouse.id,
        customerName = customer.name,
        totalAmount = totalAmount,
        status = status,
        assignedStaffName = assignedStaff?.name,
        createdAt = createdAt,
        items = items.map { it.toResponse() }.toMutableList(),
        allowedTransitions = validTransitions[status] ?: emptyList()
    )



fun OrderItem.toResponse(): OrderItemResponse =
    OrderItemResponse(
        productName = product.name,
        quantity = quantity,
        price = price
    )

fun OrderTracking.toResponse(): OrderTrackingResponse =
    OrderTrackingResponse(
        currentStatus = currentStatus,
        createdAt = createdAt,
        confirmedAt = confirmedAt,
        pickingAt = pickingAt,
        packedAt = packedAt,
        shippedAt = shippedAt,
        deliveredAt = deliveredAt,
        cancelledAt = cancelledAt
    )