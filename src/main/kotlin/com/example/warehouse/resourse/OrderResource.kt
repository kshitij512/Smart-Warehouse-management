package com.example.warehouse.resourse

import com.example.warehouse.dto.CreateOrderRequest
import com.example.warehouse.dto.OrderResponse
import com.example.warehouse.dto.OrderTrackingResponse
import com.example.warehouse.dto.UpdateOrderStatusRequest
import com.example.warehouse.model.OrderStatus
import com.example.warehouse.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Orders",
    description = "APIs for order creation, assignment, status management, and tracking"
)
@RestController
@RequestMapping("/api/orders")
class OrderResource(
    private val orderService: OrderService
) {

    @Operation(
        summary = "Create order",
        description = "Creates a new order for a warehouse with customer and product details."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Order created successfully"),
            ApiResponse(responseCode = "404", description = "Warehouse or product not found")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @Valid @RequestBody request: CreateOrderRequest
    ): OrderResponse =
        orderService.createOrder(request)

    @Operation(
        summary = "Get order by ID",
        description = "Fetches order details using order ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Order found"),
            ApiResponse(responseCode = "404", description = "Order not found")
        ]
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): OrderResponse =
        orderService.getById(id)

    @Operation(
        summary = "Get orders by warehouse",
        description = "Returns all orders belonging to a specific warehouse."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
        ]
    )
    @GetMapping("/warehouse/{warehouseId}")
    fun getByWarehouse(@PathVariable warehouseId: Long): List<OrderResponse> =
        orderService.getByWarehouse(warehouseId)

    @Operation(
        summary = "Get orders by status",
        description = "Returns all orders filtered by order status."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
        ]
    )
    @GetMapping("/status/{status}")
    fun getByStatus(@PathVariable status: OrderStatus): List<OrderResponse> =
        orderService.getByStatus(status)

    @Operation(
        summary = "Assign staff to order",
        description = "Assigns a staff member to an order."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Staff assigned successfully"),
            ApiResponse(responseCode = "404", description = "Order or staff not found")
        ]
    )
    @PutMapping("/{orderId}/assign/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun assignStaff(
        @PathVariable orderId: Long,
        @PathVariable staffId: Long
    ) =
        orderService.assignStaff(orderId, staffId)

    @Operation(
        summary = "Update order status",
        description = "Updates the current status of an order and records tracking timestamps."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Order status updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid status transition"),
            ApiResponse(responseCode = "404", description = "Order not found")
        ]
    )
    @PutMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateStatus(
        @PathVariable orderId: Long,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ) =
        orderService.updateStatus(orderId, request.status)

    @Operation(
        summary = "Get order tracking",
        description = "Returns order tracking timestamps for different order lifecycle stages."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tracking details retrieved"),
            ApiResponse(responseCode = "404", description = "Tracking not found")
        ]
    )
    @GetMapping("/{orderId}/tracking")
    fun tracking(@PathVariable orderId: Long): OrderTrackingResponse =
        orderService.getTracking(orderId)
}