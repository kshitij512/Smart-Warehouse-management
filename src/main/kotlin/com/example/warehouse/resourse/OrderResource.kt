package com.example.warehouse.resourse


import com.example.warehouse.dto.CreateOrderRequest
import com.example.warehouse.dto.OrderResponse
import com.example.warehouse.dto.OrderTrackingResponse
import com.example.warehouse.dto.UpdateOrderStatusRequest
import com.example.warehouse.model.OrderStatus
import com.example.warehouse.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderResource(
    private val orderService: OrderService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @Valid @RequestBody request: CreateOrderRequest
    ): OrderResponse =
        orderService.createOrder(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): OrderResponse =
        orderService.getById(id)

    @GetMapping("/warehouse/{warehouseId}")
    fun getByWarehouse(@PathVariable warehouseId: Long): List<OrderResponse> =
        orderService.getByWarehouse(warehouseId)

    @GetMapping("/status/{status}")
    fun getByStatus(@PathVariable status: OrderStatus): List<OrderResponse> =
        orderService.getByStatus(status)

    @PutMapping("/{orderId}/assign/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun assignStaff(
        @PathVariable orderId: Long,
        @PathVariable staffId: Long
    ) =
        orderService.assignStaff(orderId, staffId)

    @PutMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateStatus(
        @PathVariable orderId: Long,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ) =
        orderService.updateStatus(orderId, request.status)

    @GetMapping("/{orderId}/tracking")
    fun tracking(@PathVariable orderId: Long): OrderTrackingResponse =
        orderService.getTracking(orderId)
}
