package com.example.warehouse.service

import com.example.warehouse.dto.CreateOrderRequest
import com.example.warehouse.dto.OrderItemResponse
import com.example.warehouse.dto.OrderResponse
import com.example.warehouse.dto.OrderTrackingResponse
import com.example.warehouse.model.Customer
import com.example.warehouse.model.Order
import com.example.warehouse.model.OrderItem
import com.example.warehouse.model.OrderStatus
import com.example.warehouse.model.OrderTracking
import com.example.warehouse.persistence.OrderItemPersistence
import com.example.warehouse.persistence.OrderPersistence
import com.example.warehouse.persistence.OrderTrackingPersistence
import com.example.warehouse.persistence.ProductPersistence
import com.example.warehouse.persistence.UserPersistence
import com.example.warehouse.persistence.WarehousePersistence
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

    fun createOrder(request: CreateOrderRequest): OrderResponse {

        val warehouse = warehousePersistence.findByIdOrNull(request.warehouseId)
            ?: throw EntityNotFoundException("Warehouse not found")

        val order = Order(
            warehouse = warehouse,
            customer = Customer(
                name = request.customerName,
                phone = request.customerPhone,
                email = request.customerPhone,
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

        orderTrackingPersistence.save(
            OrderTracking(
                order = order,
                createdAt = LocalDateTime.now()
            )
        )

        return order.toResponse()
    }

    fun getById(orderId: Long): OrderResponse =
        orderPersistence.findByIdOrNull(orderId)
            ?.toResponse()
            ?: throw EntityNotFoundException("Order not found")

    fun getByWarehouse(warehouseId: Long): List<OrderResponse> =
        orderPersistence.findAllByWarehouseId(warehouseId)
            .map { it.toResponse() }

    fun getByStatus(status: OrderStatus): List<OrderResponse> =
        orderPersistence.findAllByStatus(status)
            .map { it.toResponse() }

    fun assignStaff(orderId: Long, staffId: Long) {
        val order = orderPersistence.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("Order not found")

        val staff = userPersistence.findByIdOrNull(staffId)
            ?: throw EntityNotFoundException("Staff not found")

        order.assignedStaff = staff
    }

    fun updateStatus(orderId: Long, status: OrderStatus) {

        val order = orderPersistence.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("Order not found")

        val tracking = orderTrackingPersistence.findByOrderId(orderId)
            ?: throw EntityNotFoundException("Tracking not found")

        // 🔐 Prevent invalid transitions
        if (order.status == OrderStatus.CANCELLED ||
            order.status == OrderStatus.DELIVERED
        ) {
            throw IllegalStateException("Order already finalized")
        }

        when (status) {

            OrderStatus.CONFIRMED -> {
                inventoryService.deductStock(
                    order.warehouse.id,
                    order.items
                )
                tracking.confirmedAt = LocalDateTime.now()
            }

            OrderStatus.CANCELLED -> {
                // rollback only if stock was deducted
                if (order.status != OrderStatus.CREATED) {
                    inventoryService.rollbackStock(
                        order.warehouse.id,
                        order.items
                    )
                }
                tracking.cancelledAt = LocalDateTime.now()
            }

            OrderStatus.PICKING -> tracking.pickingAt = LocalDateTime.now()
            OrderStatus.PACKED -> tracking.packedAt = LocalDateTime.now()
            OrderStatus.SHIPPED -> tracking.shippedAt = LocalDateTime.now()
            OrderStatus.DELIVERED -> tracking.deliveredAt = LocalDateTime.now()
            else -> {}
        }

        order.status = status
    }


    fun getTracking(orderId: Long): OrderTrackingResponse =
        orderTrackingPersistence.findByOrderId(orderId)
            ?.toResponse()
            ?: throw EntityNotFoundException("Tracking not found")
}





fun Order.toResponse(): OrderResponse =
    OrderResponse(
        id = id,
        warehouseId = warehouse.id,
        customerName = customer.name,
        totalAmount = totalAmount,
        status = status,
        assignedStaffName = assignedStaff?.name,
        createdAt = createdAt,
        items = items.map { it.toResponse() } as MutableList<OrderItemResponse>
    )

fun OrderItem.toResponse(): OrderItemResponse =
    OrderItemResponse(
        productName = product.name,
        quantity = quantity,
        price = price
    )

fun OrderTracking.toResponse(): OrderTrackingResponse =
    OrderTrackingResponse(
        createdAt,
        confirmedAt,
        pickingAt,
        packedAt,
        shippedAt,
        deliveredAt,
        cancelledAt
    )
